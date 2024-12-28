package org.example.formulaeditor.network;

import com.google.gson.Gson;
import org.example.formulaeditor.model.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NetworkServiceTest {

    private NetworkService networkServiceSpy;
    private Workbook localWorkbook;
    private SyncManager mockSyncManager;
    private Gson gson = new Gson();

    @BeforeEach
    void setup() throws Exception {
        //create a dummy workbook
        localWorkbook = new Workbook();

        //mock SyncManager
        mockSyncManager = mock(SyncManager.class);


        NetworkService realService = new NetworkService("ws://fake:1234", "peerA", localWorkbook);

        //spy on NetworkService
        networkServiceSpy = Mockito.spy(realService);

        //ignore connectivity methods
        doNothing().when(networkServiceSpy).connect();
        doNothing().when(networkServiceSpy).close();

        doAnswer(invocation -> {
            String msgJson = (String) invocation.getArguments()[0];
            System.out.println("[TEST] send() intercepted -> " + msgJson);
            return null;
        }).when(networkServiceSpy).send(anyString());
    }

    @Test
    void testOnMessage_requestWorkbook() throws Exception {
        String jsonMsg = """
        {
          "type": "REQUEST_WORKBOOK",
          "fromPeer": "peerB"
        }
        """;

        //simulate receiving JSON
        networkServiceSpy.onMessage(jsonMsg);

        //check sendWorkbookToPeer call
        verify(networkServiceSpy, times(1))
                .sendWorkbookToPeer(eq(localWorkbook), eq("peerB"));
    }

    @Test
    void testOnOpen_registersPeer() throws Exception {
        //simulate webSocket
        networkServiceSpy.onOpen(null);

        //capture register message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(networkServiceSpy, times(1)).send(captor.capture());

        String sentMsg = captor.getValue();
        Map<String, Object> msgMap = gson.fromJson(sentMsg, Map.class);

        assertEquals("REGISTER", msgMap.get("type"));
        assertEquals("peerA", msgMap.get("peerId"));
    }

    @Test
    void testOnMessage_registerAck_discoversPeers() throws Exception {
        String jsonMsg = """
        {
          "type": "REGISTER_ACK"
        }
        """;

        //simulate ack message
        networkServiceSpy.onMessage(jsonMsg);

        //check for discovery call
        verify(networkServiceSpy, times(1)).discoverPeers();
    }

    @Test
    void testOnMessage_peersList_updatesKnownPeers() throws Exception {
        String jsonMsg = """
        {
          "type": "PEERS_LIST",
          "peers": ["peerB", "peerC"]
        }
        """;

        //simulate peer list message
        networkServiceSpy.onMessage(jsonMsg);

        //check for update on peer list
        List<String> knownPeers = networkServiceSpy.getKnownPeers();
        assertEquals(2, knownPeers.size());
        assertTrue(knownPeers.contains("peerB"));
        assertTrue(knownPeers.contains("peerC"));
    }


    @Test
    void testDiscoverPeers_sendsDiscoverMessage() throws Exception {
        networkServiceSpy.discoverPeers();

        //capture discovery message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(networkServiceSpy, times(1)).send(captor.capture());

        String sentMsg = captor.getValue();
        Map<String, Object> msgMap = gson.fromJson(sentMsg, Map.class);

        assertEquals("DISCOVER", msgMap.get("type"));
        assertEquals("peerA", msgMap.get("peerId"));
    }

    @Test
    void testRequestWorkbookFrom_sendsRequestMessage() throws Exception {
        networkServiceSpy.requestWorkbookFrom("peerB");

        //capture workbook request
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(networkServiceSpy, times(1)).send(captor.capture());

        String sentMsg = captor.getValue();
        Map<String, Object> msgMap = gson.fromJson(sentMsg, Map.class);

        assertEquals("REQUEST_WORKBOOK", msgMap.get("type"));
        assertEquals("peerA", msgMap.get("peerId"));
        assertEquals("peerB", msgMap.get("targetPeerId"));
    }

    @Test
    void testSendWorkbookToPeer_sendsSignalMessage() throws Exception {
        networkServiceSpy.sendWorkbookToPeer(localWorkbook, "peerB");

        //capture signal message
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(networkServiceSpy, times(1)).send(captor.capture());

        String sentMsg = captor.getValue();
        Map<String, Object> msgMap = gson.fromJson(sentMsg, Map.class);

        assertEquals("SIGNAL", msgMap.get("type"));
        assertEquals("peerA", msgMap.get("peerId"));
        assertEquals("peerB", msgMap.get("targetPeerId"));

        Map<String, Object> dataMap = (Map<String, Object>) msgMap.get("data");
        assertNotNull(dataMap.get("workbook"));
    }


    @Test
    void testOnError_handlesException() {
        Exception ex = new Exception("Test exception");

        //simulate error
        networkServiceSpy.onError(ex);

        //check send workbook is never called
        verify(networkServiceSpy, times(0)).sendWorkbookToPeer(any(), any());
    }

}
