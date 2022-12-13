const port = 5000

// Setup express webserver
const express = require('express')
const app = express()
const {MongoClient, MongoClientOptions} = require("mongodb")

// Extend webserver with websockets
const expressWs = require('express-ws')(app)

// Setup connection to database
let db = null // global variable to hold the connection

MongoClient.connect('mongodb://canvas-db:27017/', function (err, client) {
    if (err) {
        console.error(err)
    }
    db = client.db('canvas-db') // once connected, assign the connection to the global variable

    db.listCollections().toArray(function (err, collections) {
        if (collections.findIndex(e => e.name === 'canvas') === -1) {
            console.log('Creating Canvas collection')
            db.createCollection('canvas')
        }
    })
})

const clientsInChat = new Map()

app.ws('/:chatId', (ws, req) => {

    console.log("New connection")

    const chatId = req.params.chatId
    if (chatId === undefined) {
        client.send({status: 'failed', msg: "Invalid chat id"})
        return
    }
    // Validate chatId?
    // Take in userId or token? Validate that too?
    clientsInChat.set(ws, chatId)

    ws.on('close', function () {
        console.log("Closed connection")
        clientsInChat.delete(ws)
    });
    ws.on('message', msg => {
        const data = JSON.parse(msg)

        const chatId = clientsInChat.get(ws);
        if (chatId === undefined || chatId === null) {
            client.send({status: 'failed', msg: 'Invalid chatId'})
        } else if (data.msg !== undefined && data.msg === 'REQUESTALL') {
            db.collection('canvas').find().forEach(e => {
                if (e.chatId === chatId) {
                    ws.send(JSON.stringify({status: 'success', data: e.data}))
                }
            })
        } else {
            db.collection('canvas').insertOne({chatId: chatId, data: data})
            expressWs.getWss('/').clients.forEach(client => {
                if (clientsInChat.get(client) === chatId && client !== ws) {
                    client.send(JSON.stringify({status: 'success', data: JSON.parse(msg)}))
                }
            })
        }

    })
})

app.listen(port, () => {
    console.log(`Listening at http://localhost:${port}`)
})

