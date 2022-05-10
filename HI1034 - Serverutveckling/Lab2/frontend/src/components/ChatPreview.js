import {Card} from "react-bootstrap";

const ChatPreview = ({name, lastMessageSent, onClick}) => {
    return (
        <Card className={"zoom"} onClick={onClick} style={{
            width: '100%',
            marginTop: '10px'
        }}>
            <Card.Body>
                <Card.Title>{name}</Card.Title>
            </Card.Body>
        </Card>
    )
}

export default ChatPreview