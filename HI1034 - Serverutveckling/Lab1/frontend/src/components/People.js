import {Button, Card, Container, Form} from "react-bootstrap";
import {useEffect, useState} from "react";
import {fetch} from "whatwg-fetch";
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import {fetchPosts, Post} from "./Post";

const addFriend = async (id, token, onError) => {
    await fetch('http://localhost:8080/users/friends', {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: token,
            friendId: id
        })
    })
        .then(response => {
            if (!response.ok) {
                throw response
            }
        })
        .catch(_ => onError('Misslyckade att lägga till vän'))
}

const removeFriend = async (id, token, onError) => {
    await fetch('http://localhost:8080/users/friends', {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: token,
            friendId: id
        })
    })
        .then(response => {
            if (!response.ok) {
                throw response
            }
        })
        .catch(_ => onError('Misslyckade att ta bort vän'))
}


const fetchFriends = (setFriends, token, onError) => {
    console.log("hel")
    fetch('http://localhost:8080/users/' + token + '/friends', {
        method: "GET",
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
        .then(jsonResponse => {
            // setError(null)
            setFriends(jsonResponse)
        })
        .catch(_ => onError('Misslyckade att hämta vänner'))
}

const fetchUsers = async (searchFor, setUsers, authUserId, onError) => {
    if (searchFor == null || searchFor === "") {
        setUsers([])
    } else {
        fetch('http://localhost:8080/users?searchFor=' + searchFor, {
            method: "GET",
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
            .then(jsonResponse => {
                // setError(null)
                jsonResponse = jsonResponse.filter(u => u.id !== authUserId)
                setUsers(jsonResponse)
            })
            .catch(_ => onError('Misslyckade att hämta användare'))
    }
}


const PeopleCard = ({user, onAddFriend, onRemoveFriend, onViewPosts, isFriend}) => {
    return (
        <Card className={'good-card'}>
            <Card.Body>
                <Card.Title>{user.name}</Card.Title>
                <Card.Text>{user.email}</Card.Text>
                <Button hidden={isFriend} onClick={() => onAddFriend(user.id)}> Lägg till vän </Button>
                <Button variant={'danger'} hidden={!isFriend} onClick={() => onRemoveFriend(user.id)}>
                    Ta bort vän
                </Button>
                <Button variant={'secondary'} onClick={() => onViewPosts(user.id)}>
                    Visa inlägg
                </Button>
            </Card.Body>
        </Card>
    )
}


const PostView = ({posts, onWantBack}) => (
    <Container className="d-grid gap-2">
        <Button style={{
            width: "25rem",
            marginRight: "auto",
            marginLeft: "auto",
            marginTop: "1rem"
        }} variant="primary" onClick={onWantBack}>
            Gå tillbaka
        </Button>
        {posts.map(p => (
            <Post content={p.content} created={p.created} creator={p.creator} imageId={p.imageId}/>)
        )}
    </Container>)

const SearchView = ({users, friends, onSearch, onAddFriend, onRemoveFriend, onViewPosts}) => (
    <Container className="d-grid gap-2">
        <Form>
            <Form.Control placeholder={'Sök på namn eller e-post'}
                          onChange={onSearch}
                          className={'good-card'}
                          type={'text'}/>
        </Form>
        {
            users.map(u => (<PeopleCard key={u.email}
                                        user={u}
                                        onAddFriend={onAddFriend}
                                        onRemoveFriend={onRemoveFriend}
                                        onViewPosts={onViewPosts}
                                        isFriend={friends.find(f => f.id === u.id)}/>))
        }
    </Container>)

const People = ({authUser}) => {
        const [displaySearch, setDisplaySearch] = useState(true)
        const [error, setError] = useState(null)

        const [posts, setPosts] = useState([])
        const [, setLoading] = useState(true);

        const [users, setUsers] = useState([])
        const [friends, setFriends] = useState([])
        const [failAlertVisible, setFailAlertVisible] = useState(false)

        const onError = (err) => {
            setFriends([])
            setUsers([])
            setError(err)
            handleAlertVisibility(setFailAlertVisible)
        }

        useEffect(() => {
            fetchFriends(setFriends, authUser.token, onError)
        }, [authUser.token])

        const handleAddFriend = async (id) => {
            await addFriend(id, authUser.token, onError)
            fetchFriends(setFriends, authUser.token, onError)
        }

        const handleRemoveFriend = async (id) => {
            await removeFriend(id, authUser.token, onError)
            fetchFriends(setFriends, authUser.token, onError)
        }

        const handleViewPosts = async (id) => {
            fetchPosts((posts) => {
                setDisplaySearch(false)
                setPosts(posts)
            }, setError, setLoading, null, id)
        }

        return (
            <>
                <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>
                {displaySearch ?
                    <SearchView
                        users={users}
                        friends={friends}
                        onSearch={async e => await fetchUsers(e.target.value, setUsers, authUser.id, onError)}
                        onViewPosts={handleViewPosts}
                        onAddFriend={handleAddFriend}
                        onRemoveFriend={handleRemoveFriend}
                    /> :
                    <PostView
                        posts={posts}
                        onWantBack={() => {
                            setDisplaySearch(true)
                        }}
                    />}
            </>
        )
    }
;

export default People