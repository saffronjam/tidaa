import {Card, Container} from "react-bootstrap";
import {fetch} from "whatwg-fetch";
import {IMAGE_MS_IP, POST_MS_IP, USER_MS_IP} from "../Constants";
import {useEffect, useState} from "react";
import {Reports} from "./Reports";

export const fetchPosts = (setPosts, setError, setLoading, userId = -1, userIds = null) => {
    let link = ""
    if (userId !== -1) {
        link = `${POST_MS_IP}/posts/${userId}`;
    } else if (userIds != null) {
        link = `${POST_MS_IP}/posts?userIds=${userIds.join()}`;
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

const fetchUser = (userId, setUsername) => {
    fetch(`${USER_MS_IP}/users/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(res => {
        if (res.ok) {
            return res.json()
        }
        throw res
    }).then(res => {
        setUsername(res.name)
    }).catch(_ => ({
        name: 'Kunde inte hämta användare'
    }))
}

const Image = ({imageId}) => {

    return (
        <Container style={{marginBottom: "1rem"}}>
            <Card.Img variant="top"
                      src={`${IMAGE_MS_IP}/images/${imageId}`}
                      style={{borderRadius: "8px"}}
            />
        </Container>
    )
}

const dateFormatOptions = {
    hour: '2-digit',
    minute: '2-digit',
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
};

export const Post = ({content, imageId, reportsId, creatorId, created}) => {
    const [username, setUsername] = useState('Loading...')

    useEffect(() => {
        fetchUser(creatorId, setUsername)
    }, [creatorId])

    return (
        <Card className="zoom" style={{width: '36rem', marginRight: "auto", marginLeft: "auto", marginTop: "1rem"}}>
            <Card.Body>
                <Container className="d-flex justify-content-between">
                    <Card.Text>{content}</Card.Text>
                    <Card.Text>{username}</Card.Text>
                </Container>
            </Card.Body>

            {
                imageId === -1 ?
                    <></> :
                    <Image imageId={imageId}/>
            }
            {
                reportsId === "-1" ?
                    <></> :
                    <Reports reportsId={reportsId}/>
            }
            <Card.Footer>
                <Card.Text>{created.toLocaleDateString('sv', dateFormatOptions)}</Card.Text>
            </Card.Footer>
        </Card>
    )
}
