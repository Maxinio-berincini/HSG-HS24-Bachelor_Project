package org.example.formulaeditor.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NetworkService extends WebSocketClient {
    private static final Gson GSON = new Gson();

    private final String peerId;
    private final SyncManager syncManager;
    private Workbook localWorkbook;
    private List<String> knownPeers = new ArrayList<>();
    private int pendingRequests = 0;
    private boolean pullInProgress = false;

    public NetworkService(String serverUri, String peerId, Workbook localWorkbook) throws Exception {
        super(new URI(serverUri));
        this.peerId = peerId;
        this.localWorkbook = localWorkbook;
        this.syncManager = SyncManager.getInstance();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[NetworkService] WebSocket connected. Registering peer: " + peerId);

        // register on signaling server
        String registerMsg = GSON.toJson(Map.of(
                "type", "REGISTER",
                "peerId", peerId
        ));
        send(registerMsg);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[NetworkService] Message received: " + message);
        try {
            Map<String, Object> msg = GSON.fromJson(message, new TypeToken<Map<String, Object>>(){}.getType());
            String type = (String) msg.get("type");

            switch (type) {
                case "REGISTER_ACK" -> {
                    System.out.println("[NetworkService] Registered on server. Discovering peers...");
                    discoverPeers();
                }
                case "PEERS_LIST" -> {
                    List<String> peersList = (List<String>) msg.get("peers");
                    knownPeers.clear();
                    knownPeers.addAll(peersList);
                    System.out.println("[NetworkService] Available peers: " + knownPeers);
                }
                case "REQUEST_WORKBOOK" -> {
                    //send local workbook on pull request
                    String fromPeer = (String) msg.get("fromPeer");
                    System.out.println("[NetworkService] Received REQUEST_WORKBOOK from " + fromPeer);

                    //signal local workbook
                    sendWorkbookToPeer(localWorkbook, fromPeer);
                }
                case "SIGNAL" -> {
                    // receiving workbook as json
                    Map<?, ?> data = (Map<?, ?>) msg.get("data");
                    if (data.containsKey("workbook")) {
                        String workbookJson = (String) data.get("workbook");

                        // json to dto
                        WorkbookSyncDTO incomingDTO = GSON.fromJson(workbookJson, WorkbookSyncDTO.class);

                        // dto to workbook
                        Workbook remoteWorkbook = NetworkSerializer.fromSyncDTO(incomingDTO, new Parser());

                        System.out.println("[NetworkService] Merging remote workbook into local...");
                        syncManager.merge(localWorkbook, remoteWorkbook);
                        System.out.println("[NetworkService] Merge complete.");

                        //on multiple pulls
                        if (pendingRequests > 0) {
                            pendingRequests--;
                            System.out.println("[NetworkService] pendingRequests: " + pendingRequests);

                            //broadcast on final pull
                            if (pendingRequests == 0 && pullInProgress) {
                                broadcastLocalWorkbook();
                                pullInProgress = false;
                            }
                        }
                        //if not pull in progress, maybe a broadcast --> avoid broadcasting loop
                    }
                }
                default -> System.out.println("[NetworkService] Unknown message type: " + type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[NetworkService] WebSocket closed. Code: " + code + ", Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[NetworkService] WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }


    public void discoverPeers() {
        String discoverMsg = GSON.toJson(Map.of("type", "DISCOVER", "peerId", peerId));
        send(discoverMsg);
    }


    public void requestWorkbookFrom(String targetPeerId) {
        Map<String, Object> reqMsg = Map.of(
                "type", "REQUEST_WORKBOOK",
                "peerId", peerId,
                "targetPeerId", targetPeerId
        );
        send(GSON.toJson(reqMsg));
        System.out.println("[NetworkService] REQUEST_WORKBOOK sent to " + targetPeerId);
    }

    public void pullFromAllAndBroadcast() {
        pullInProgress = true;
        discoverPeers();

        //wait for updated peer list before pulling
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                requestFromAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void requestFromAll() {
        System.out.println("[NetworkService] Pulling from peers: " + knownPeers);
        pendingRequests = 0;

        for (String p : knownPeers) {
            if (!p.equals(peerId)) {
                requestWorkbookFrom(p);
                pendingRequests++;
            }
        }
        if (pendingRequests == 0 && pullInProgress) {
            broadcastLocalWorkbook();
            pullInProgress = false;
        }
    }

    private void broadcastLocalWorkbook() {
        System.out.println("[NetworkService] Broadcasting final merged local workbook");
        sendWorkbookToPeer(localWorkbook, "BROADCAST");
    }

    public void sendWorkbookToPeer(Workbook workbook, String targetPeerId) {
        //convert to DTO
        WorkbookSyncDTO dto = NetworkSerializer.toSyncDTO(workbook);
        String wbJson = GSON.toJson(dto);

        Map<String, Object> signalMsg = Map.of(
                "type", "SIGNAL",
                "peerId", peerId,
                "targetPeerId", targetPeerId,
                "data", Map.of("workbook", wbJson)
        );
        String signalMsgStr = GSON.toJson(signalMsg);
        send(signalMsgStr);

        System.out.println("[NetworkService] Sent workbook to peer: " + targetPeerId);
    }


    public void waitForConnection() throws InterruptedException {
        int retries = 0;
        while (getReadyState() != ReadyState.OPEN && retries < 5) {
            System.out.println("[NetworkService] Waiting for WebSocket to open...");
            TimeUnit.SECONDS.sleep(1);
            retries++;
        }
    }

    public Workbook getLocalWorkbook() {
        return localWorkbook;
    }

    public void setLocalWorkbook(Workbook localWorkbook) {
        this.localWorkbook = localWorkbook;
    }
}
