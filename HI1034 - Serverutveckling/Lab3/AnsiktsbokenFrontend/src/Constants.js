const isNotHeroku = !process.env.REACT_APP_HEROKU
console.log("Using Heroku: " + !isNotHeroku)
export const CHAT_MS_IP = isNotHeroku ? 'http://localhost:8081' : 'https://serverlabb3-chatMs.herokuapp.com'
export const IMAGE_MS_IP = isNotHeroku ? 'http://localhost:8082' : 'https://serverlabb3-imageMs.herokuapp.com'
export const POST_MS_IP = isNotHeroku ? 'http://localhost:8083' : 'https://serverlabb3-postMs.herokuapp.com'
export const USER_MS_IP = isNotHeroku ? 'http://localhost:8084' : 'https://serverlabb3-userMs.herokuapp.com'
export const VERTX_MS_IP = isNotHeroku ? 'http://localhost:8086' : 'https://serverlabb3-vertxMs.herokuapp.com'
export const CANVAS_MS_WS_IP = isNotHeroku ? 'ws://localhost:5000' : 'wss://serverlabb3-canvasMs.herokuapp.com'
