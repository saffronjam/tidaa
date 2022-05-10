import {useEffect, useState} from "react";
import {fetch} from "whatwg-fetch";
import ChatPreview from "./ChatPreview";
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import {Container} from "react-bootstrap";
import Chat from "./Chat";
import {CHAT_MS_IP} from "../Constants";

const fetchAllPreviews = async (onError, token) => {
    return fetch(`${CHAT_MS_IP}/chats/${token}`, {
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
        .catch(_ => onError("Misslyckade att hämta konversationsförhandvisningarna")
        )
}

const chatNames = new Map()

const Chats = ({authUser}) => {
    const [error, setError] = useState(null)
    const [chatPreviews, setChatPreviews] = useState([])
    const [failAlertVisible, setFailAlertVisible] = useState(false)
    const [chats, setChats] = useState([])

    const onError = (err) => {
        handleAlertVisibility(setFailAlertVisible)
        setError(err)
    }

    useEffect(() => {
        fetchAllPreviews(onError, authUser.token).then(previews => {
            previews.forEach(p => chatNames.set(p.id, p.name))
            setChatPreviews(previews)
        })
    }, [authUser.token]);

    const handleChatPreviewClick = (chatId) => {
        if (chats.includes(chatId)) {
            setChats(chats.filter(id => id !== chatId))
        } else {
            setChats([...chats, chatId])
        }
    }

    return (<>
        <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>

        <Container style={{
            display: "flex",
            justifyContent: "space-between",
            width: "100%",
            marginBottom: "20px",
            overflowY: "auto"
        }} >
            <div style={{
                width: "30%",
                left: "0px"
            }}>
                {chatPreviews.map(p => (
                    <ChatPreview stly key={p.id} name={p.name} onClick={() => handleChatPreviewClick(p.id)}/>)
                )}
            </div>

            <div style={{
                width: "60%",
                right: "0px"
            }}>
                {chats.map(id => <Chat key={id} authUser={authUser} name={chatNames.get(id)} chatId={id} token={authUser.token}/>)}
            </div>
        </Container>

    </>)
};

export default Chats