import {Button, Form, Image} from "react-bootstrap";
import FileUploader from "./FileUploader";

const Chat = ({chat, setText, onSend, onImageSelect}) => {
    return (
        <>
            {(<h1>{chat.friend.name}</h1>)}
            {chat.messages.map(m => (
                <>
                    <p><b>{m.sender.name}</b>: {m.content}</p>
                    {m.imageId !== -1 ? <Image
                        src={`http://localhost:8080/images/${m.imageId}`}
                        style={{borderRadius: "4px"}}
                        alt={'chat_image'}/> : <></>
                    }
                </>
            ))}
            <Form>
                <Form.Group onChange={e => setText(e.target.value)}
                            controlId={"exampleForm.ControlText"}
                            className={"mb-3"}>
                    <Form.Control placeholder={'Meddelande'}
                                  style={{marginRight: '3px'}}
                    />
                </Form.Group>
                <Button onClick={() => onSend()}>
                    Skicka
                </Button>
                <FileUploader className='noselect' onSelect={(file) => onImageSelect(file)}>
                    Ladda upp bild
                </FileUploader>
            </Form>
        </>)
}

export default Chat;