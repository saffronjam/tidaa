import {Card, Container} from "react-bootstrap";
import {fetch} from "whatwg-fetch";

export const fetchPosts = (setPosts, setError, setLoading, token = null, userId = -1) => {
    let link = ""
    if (token) {
        link = "http://localhost:8080/posts/" + token + "/friends"
    } else if (userId !== -1) {
        link = "http://localhost:8080/posts/" + userId;
    }

    fetch(link, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(res => res.json())
        .then(res => {
            res.map(p => {
                p.created = new Date(p.created)
                return p;
            })
            setPosts(res)
        })
        .catch(err => {
            setError(err)
        })
        .finally(() => {
            setLoading(false)
        })
}

export const Post = ({content, imageId, creator, created}) => {
    const dateFormatOptions = {
        hour: '2-digit',
        minute: '2-digit',
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    };

    return (
        <Card className="zoom" style={{width: '36rem', marginRight: "auto", marginLeft: "auto", marginTop: "1rem"}}>
            <Card.Body>
                <Container className="d-flex justify-content-between">
                    <Card.Text>{content}</Card.Text>
                    <Card.Text>{creator.name}</Card.Text>
                </Container>
            </Card.Body>

            {
                imageId === -1 ?
                    <></> :
                    <Container style={{marginBottom: "1rem"}}>
                        <Card.Img variant="top"
                                  src={`http://localhost:8080/images/${imageId}`}
                                  style={{borderRadius: "8px"}}
                        />
                    </Container>
            }
            <Card.Footer>
                <Card.Text>{created.toLocaleDateString('sv', dateFormatOptions)}</Card.Text>
            </Card.Footer>
        </Card>
    )
}
