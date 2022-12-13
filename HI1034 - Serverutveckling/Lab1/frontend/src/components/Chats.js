import {useEffect, useState} from "react";
import {fetch} from "whatwg-fetch";
import ChatPreview from "./ChatPreview";
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import {Col, Container, Row} from "react-bootstrap";
import Chat from "./Chat";

const fetchAllPreviews = (setChatPreviews, onError, token) => {
    fetch('http://localhost:8080/chats/' + token, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json()
            }
            throw response
        })
        .then(jsonResponse => setChatPreviews(jsonResponse))
        .catch(_ => onError("Misslyckade att hämta konversationsförhandvisningarna")
        )
}

const fetchChat = (token, friendId, setChat, onError) => {
    fetch('http://localhost:8080/chats/' + token + '/' + friendId, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json()
            } else {
                throw response
            }
        })
        .then(jsonResponse => setChat(jsonResponse))
        .catch(_ => onError("Misslyckade att hämta konversationen")
        )
}

const sendMessage = (token, chatId, message, image, onSuccess, onError) => {
    let formData = new FormData();
    formData.append("content", message)
    formData.append("chatId", chatId)
    formData.append("token", token)
    if (image) {
        formData.append("image", image)
    }

    fetch('http://localhost:8080/messages', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (!response.ok) {
            throw response
        }
        onSuccess()
    }).catch(_ => {
        onError("Misslyckade att skicka meddelande")
    })
}

const Chats = ({authUser}) => {
    const [error, setError] = useState(null)
    const [chatPreviews, setChatPreviews] = useState([])
    const [failAlertVisible, setFailAlertVisible] = useState(false)
    const [chat, setChat] = useState(null)
    const [text, setText] = useState('')
    const [image, setImage] = useState(null)

    const onError = (err) => {
        handleAlertVisibility(setFailAlertVisible)
        setError(err)
    }

    useEffect(() => {
        let intervalId = setInterval(() => {
            if (chat) {
                fetchChat(authUser.token, chat.friend.id, setChat, onError)
            }
        }, 1000);

        fetchAllPreviews(setChatPreviews, onError, authUser.token)
        return () => {
            clearInterval(intervalId)
        }
    }, [chat, authUser.token]);

    const handleSend = () => {
        sendMessage(authUser.token, chat.id, text, image, () => {
            fetchChat(authUser.token, chat.friend.id, setChat, onError)
        }, onError)
    }

    const handleImageSelect = (image) => {
        setImage(image)
    }

    return (<>
        <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>

        <Container>
            <Row>
                <Col>
                    {chatPreviews.map(p => (
                        <ChatPreview onClick={() => fetchChat(authUser.token, p.friend.id, setChat, onError)}
                                     name={p.friend.name}/>)
                    )}
                </Col>
                <Col xs={6}>
                    <Container>
                        {chat ? <Chat chat={chat}
                                      setText={setText}
                                      onSend={() => handleSend()}
                                      onImageSelect={(image) => handleImageSelect(image)}
                        /> : <></>}
                    </Container>
                </Col>
            </Row>
        </Container>

    </>)
};

export default Chats