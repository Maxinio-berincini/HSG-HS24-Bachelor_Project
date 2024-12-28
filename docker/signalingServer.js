const originalLog = console.log;
const originalWarn = console.warn;
const originalError = console.error;

console.log = (...args) => {
    originalLog(`[${new Date().toISOString()}]`, ...args);
};
console.warn = (...args) => {
    originalWarn(`[${new Date().toISOString()}] WARN:`, ...args);
};
console.error = (...args) => {
    originalError(`[${new Date().toISOString()}] ERROR:`, ...args);
};

const WebSocket = require('ws');

//websocket on port 2425
const wss = new WebSocket.Server({ port: 2425 });

//list of available peers
let peers = {};

console.log('Signaling server started on wss://helix.berinet.ch');

wss.on('connection', (ws) => {
    console.log('New client connected');

    ws.on('message', (message) => {
        try {
            const msg = JSON.parse(message);
            console.log('Received message:', msg);

            if (msg.type === 'REGISTER') {
                //register peer
                peers[msg.peerId] = ws;
                console.log(`Peer registered: ${msg.peerId}`);
                ws.send(JSON.stringify({ type: 'REGISTER_ACK', message: 'Registered successfully' }));
            }

            if (msg.type === 'DISCOVER') {
                //send list of available peers
                const availablePeers = Object.keys(peers).filter(id => id !== msg.peerId);
                ws.send(JSON.stringify({ type: 'PEERS_LIST', peers: availablePeers }));
                console.log(`Sent peer list to ${msg.peerId}:`, availablePeers);
            }

            if (msg.type === 'SIGNAL') {
                //forward message
                const fromPeer = msg.peerId;
                const targetPeerId = msg.targetPeerId;

                //send to all on broadcast
                if (targetPeerId === 'BROADCAST') {
                    console.log(`Broadcasting signal from ${fromPeer} to all peers`);
                    Object.entries(peers).forEach(([peerId, socket]) => {
                        if (peerId !== fromPeer) {
                            socket.send(JSON.stringify({
                                type: 'SIGNAL',
                                from: fromPeer,
                                data: msg.data,
                            }));
                        }
                    });
                } else {
                    //send to single peer
                    const target = peers[targetPeerId];
                    if (target) {
                        console.log(`Relaying signal from ${fromPeer} to ${targetPeerId}`);
                        target.send(JSON.stringify({
                            type: 'SIGNAL',
                            from: fromPeer,
                            data: msg.data,
                        }));
                    } else {
                        console.warn(`Target peer not found: ${targetPeerId}`);
                    }
                }
            }
        } catch (err) {
            console.error('Error processing message:', err);
        }
    });

    ws.on('close', () => {
        //remove on disconnect
        for (const [id, socket] of Object.entries(peers)) {
            if (socket === ws) {
                delete peers[id];
                console.log(`Peer disconnected: ${id}`);
                break;
            }
        }
    });

    ws.on('error', (error) => {
        console.error('WebSocket error:', error);
    });
});
