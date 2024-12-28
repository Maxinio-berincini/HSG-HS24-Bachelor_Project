FROM node:18-alpine
LABEL authors="Max Beringer"
WORKDIR /usr/src/app
COPY signalingServer.js .
RUN npm install ws
EXPOSE 2425
CMD ["node", "signalingServer.js"]
