import {Card} from "react-bootstrap";

const ChatPreview = ({name, lastMessageSent, onClick}) => {
    return (
        <Card style={{width: '18rem'}} onClick={onClick}>
            <Card.Body>
                <Card.Title>{name}</Card.Title>
            </Card.Body>
        </Card>
    )
}

export default ChatPreview