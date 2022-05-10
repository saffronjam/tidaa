import {Button, Card, Container, Form, Image, Spinner} from "react-bootstrap";
import FileUploader from "./FileUploader";
import {CHAT_MS_IP, IMAGE_MS_IP, USER_MS_IP} from "../Constants";
import {useEffect, useState} from "react";
import {fetch} from "whatwg-fetch";
import Canvas from "./Canvas";
import {Reports, fetchUserLogins} from "./Reports";
import CardHeader from "react-bootstrap/CardHeader";
import $ from 'jquery';

const scrollToBottom = (id, animate = true) => {
    setTimeout(() => {
        const div = document.getElementById(id);
        if (div) {
            if (animate) {
                $('#' + id).animate({
                    scrollTop: div.scrollHeight - div.clientHeight
                }, 150);
            } else {
                div.scrollTop = div.scrollHeight - div.clientHeight;
            }
        }
    }, 300)
};

const fetchUsername = (userId, setUsername) => {
    fetch(`${USER_MS_IP}/users/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(res => {
        if (res.ok) {
            return res.json()
        }
    }).then(res => {
        setUsername(res.name)
    }).catch(_ => {
    })
}

const Message = ({message, usernameCache}) => {
    const [username, setUsername] = useState('Loading...')
    const [messageSent] = useState(new Date(message.sent))

    useEffect(() => {
        if (usernameCache.has(message.senderId)) {
            setUsername(usernameCache.get(message.senderId))
        } else {
            fetchUsername(message.senderId, (username) => {
                usernameCache.set(message.senderId, username)
                setUsername(username)
            })
        }
    }, [message, usernameCache])

    return (
        <>
            {messageSent === undefined ?
                <>undef</>
                :
                <p>{`${messageSent.getHours()}:${messageSent.getMinutes()} `}
                    <b>{username}</b>: {message.content}
                </p>
            }
            {message.imageId === -1 ?
                <></>
                :
                <Image
                    src={`${IMAGE_MS_IP}/images/${message.imageId}`}
                    alt={'chat_image'}
                    style={{
                        borderRadius: "4px",
                        width: "400px"
                    }}/>
            }
            {
                message.reportsId === "-1" ?
                    <></>
                    :
                    <Reports reportsId={message.reportsId}/>
            }
        </>
    )
}

const fetchChatData = (token, chatId) => {
    return fetch(`${CHAT_MS_IP}/chats/${token}/${chatId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            return response.json()
        }
        throw response
    }).catch(_ => ({
        name: 'Failed to fetch chat',
        messages: null
    }))
}

const sendMessage = async (token, chatId, message, image, reports) => {
    let formData = new FormData();
    formData.append('content', message)
    formData.append('chatId', chatId)
    formData.append('token', token)
    if (image) {
        formData.append('image', image)
    }
    formData.append('reportsString', JSON.stringify(reports))

    return fetch(`${CHAT_MS_IP}/messages`, {
        method: 'POST',
        body: formData
    }).then(res => {
        if (!res.ok) {
            throw res
        }
    })
}

const Chat = ({authUser, chatId, name}) => {
    const [usernameCache] = useState(new Map())
    const [chatData, setChatData] = useState({
        name: name,
        messages: null
    })

    const [text, setText] = useState('')
    const [image, setImage] = useState(null)
    const [, setImageValue] = useState('')

    const [viewCanvas, setViewCanvas] = useState(false)
    const [wantReports, setWantReports] = useState(false)

    useEffect(() => {
        let intervalId = setInterval(() => {
            fetchChatData(authUser.token, chatId, setChatData).then(chatData => {
                setChatData(chatData)
            })
        }, 1000);

        fetchChatData(authUser.token, chatId, setChatData).then(chatData => {
            setChatData(chatData)
            scrollToBottom(`chatScroll-${chatId}`, false)
        })

        return () => {
            clearInterval(intervalId)
        }
    }, [chatId, authUser.token])

    const handleSendSubmit = async (event) => {
        event.preventDefault();

        if (text.length === 0 && !wantReports && !image) {
            return
        }

        let reports = []
        if (wantReports) {
            reports = await fetchUserLogins(authUser.id)
                .catch(_ => [])
        }

        // Send to Api
        await sendMessage(authUser.token, chatId, text, image, reports).then(() => {
            fetchChatData(authUser.token, chatId).then(chatData => {
                setChatData(chatData)
                scrollToBottom(`chatScroll-${chatId}`)
            })

            setImage(null)
            setImageValue('')
            setWantReports(false)
            setText('')
        }).catch(_ => _)
    }


    const handleImageSelect = (image) => {
        setImage(image)
    }

    return (
        <>
            <Card style={{
                marginTop: "10px",
                paddingBottom: "10px"
            }}>
                <CardHeader>{chatData.name} </CardHeader>
                <Container style={{
                    width: "500px"
                }}>
                    <div id={`chatScroll-${chatId}`} style={{
                        width: "100%",
                        height: "400px",
                        overflow: "auto"
                    }}>
                        {
                            chatData.messages === null ?
                                <Spinner style={{
                                    display: "flex",
                                    margin: "50% auto",
                                    justifyContent: "center",
                                    alignItems: "center"

                                }} animation="border" role="status"/>
                                :
                                chatData.messages.map(m => (
                                    <Message message={m} usernameCache={usernameCache}/>
                                ))
                        }
                    </div>
                    <Form onSubmit={handleSendSubmit}>
                        <Form.Group style={{marginBottom: "10px"}}
                                    onChange={e => setText(e.target.value)}
                                    controlId={"exampleForm.ControlText"}
                                    className={"mb-3"}>

                            <div style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                marginTop: '10px'
                            }}>

                                <FileUploader className='noselect'
                                              disabled={chatData.messages === null}
                                              onSelect={(file) => handleImageSelect(file)}/>

                                <Form.Control disabled={chatData.messages === null}
                                              value={text}
                                              placeholder={'Meddelande'}
                                              autoComplete={'off'}
                                              style={{marginRight: '3px', boxShadow: 'none'}}/>

                                <label htmlFor={'sendbtn'}>
                                    <Button id={'sendbtn'} disabled={chatData.messages === null}
                                            onClick={handleSendSubmit}
                                            style={{display: 'none'}}>
                                        Skicka
                                    </Button>
                                    <img
                                        src={'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Feather-arrows-arrow-right.svg/1200px-Feather-arrows-arrow-right.svg.png'}
                                        alt={'send-btn'}
                                        style={{
                                            height: '3rem'
                                        }}/>
                                </label>


                            </div>
                        </Form.Group>


                        <Container style={{
                            display: "flex",
                            alignItems: "center"
                        }}>
                            <Form.Group style={{
                                display: "flex",
                                alignItems: "center"
                            }}>
                                <Form.Text style={{
                                    margin: "5px"
                                }}>Visa ritbräde</Form.Text>
                                <Form.Check disabled={chatData.messages === null}
                                            checked={viewCanvas}
                                            onChange={e => setViewCanvas(e.target.checked)}/>
                            </Form.Group>
                            <Form.Group style={{
                                display: "flex",
                                alignItems: "center",
                                marginLeft: "15px"
                            }}>
                                <Form.Text style={{
                                    margin: "5px"
                                }}>Skicka inloggningsgraf</Form.Text>
                                <Form.Check disabled={chatData.messages === null}
                                            checked={wantReports}
                                            onChange={e => setWantReports(e.target.checked)}/>
                            </Form.Group>
                        </Container>

                        {
                            viewCanvas ?
                                <div style={{
                                    position: 'relative'
                                }}>
                                    <Canvas chatId={chatId}/>
                                </div>
                                :
                                <></>
                        }

                    </Form>
                </Container>
            </Card>
        </>)
}

export default Chat;