const proxy = require('express-http-proxy');
const Cookies = require( "cookies" )
const cookieParser = require('cookie-parser')

const express = require('express');
const path = require('path');
const app = express();

const AUTH_SERVER_URL = "http://localhost:8080"
const API_GATEWAY_URL = "http://localhost:9000"

app.use(cookieParser())
app.use(express.static(path.join(__dirname, 'build')));
app.use((req, res, next) => {
    res.set('Cache-Control', 'no-store, no-cache, must-revalidate, private')
    next()
})

app.all('/auth/*', proxy(AUTH_SERVER_URL));

app.use('/*', (req, res, next) => {

    const jwsToken = req.cookies["jwt-token"]
    if (jwsToken) {
        req.headers.Authorization = "Bearer " + jwsToken;
    }

    next();
});

app.all('/offer-frontend/*', proxy(API_GATEWAY_URL));
app.all('/loan-frontend/*', proxy(API_GATEWAY_URL));
app.all('/offer/*', proxy(API_GATEWAY_URL));
app.all('/loan/*', proxy(API_GATEWAY_URL));
app.all('/user/*', proxy(API_GATEWAY_URL));
app.all('/borrowerLoan/*', proxy(API_GATEWAY_URL));

app.get('/', function (req, res) {
    res.sendFile(path.join(__dirname, 'build', 'index.html'));
});

app.listen(process.env.PORT || 9900);


