import {Card, Container} from "react-bootstrap";

const Profile = ({authUser}) => {
    return (
        <Container style={{width: '36rem', marginRight: "auto", marginLeft: "auto", marginTop: "1rem"}}>
            <Card style={{width: '18rem', marginBottom: '1rem'}}>
                <Card.Body>
                    <Card.Title>{authUser.name}</Card.Title>
                    <Card.Text>{authUser.email}</Card.Text>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Profile