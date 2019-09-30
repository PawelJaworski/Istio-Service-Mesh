FROM node:8.16.0-alpine

WORKDIR /app

COPY ./dist/ /app

RUN npm install -g serve

EXPOSE 7001

ADD entrypoint.sh /entrypoint.sh

ENTRYPOINT [ "serve", "-p", "7001" ]