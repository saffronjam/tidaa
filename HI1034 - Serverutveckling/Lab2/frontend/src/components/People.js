import {Button, Card, Container, Form, FormCheck} from "react-bootstrap";
import {useEffect, useState} from "react";
import {fetch} from "whatwg-fetch";
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import {fetchPosts, Post} from "./Post";
import {CHAT_MS_IP, USER_MS_IP} from "../Constants";

const central = {
    width: '25rem',
    marginRight: "auto",
    marginLeft: "auto",
    marginTop: "1rem"
}

const addFriend = async (id, token, onError) => {
    await fetch(`${USER_MS_IP}/users/friends`, {
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
    await fetch(`${USER_MS_IP}/users/friends`, {
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
    fetch(`${USER_MS_IP}/users/${token}/friends`, {
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
        fetch(`${USER_MS_IP}/users?searchFor=${searchFor}`, {
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


const createChat = (setError, onSuccess, token, name, checkedUsers) => {
    fetch(`${CHAT_MS_IP}/chats`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            token: token,
            name: name,
            members: checkedUsers
        })
    }).then(youare => {
        if (youare.ok) {
            onSuccess()
        }
        throw youare
    }).catch(_ => setError('Misslyckade att skapa chat'))
}

const PeopleCard = ({user, onAddFriend, onRemoveFriend, onViewPosts, isFriend, onChecked}) => {
    return (
        <Card className={'good-card'}>
            <Card.Body>
                <Card.Title>{user.name}</Card.Title>
                <Card.Text>{user.email}</Card.Text>
                <Button hidden={isFriend} onClick={() => onAddFriend(user.id)}> Lägg till vän </Button>
                <Button variant={'danger'} hidden={!isFriend} onClick={() => onRemoveFriend(user.id)}>
                    Ta bort vän
                </Button>
                <Button variant={'secondary'} onClick={() => onViewPosts(user.id)} style={{
                    marginLeft: "5px"
                }}>
                    Visa inlägg
                </Button>
                <Form.Group>
                    <Form.Text>Markera för konversation</Form.Text>
                    <Form.Check onChange={e => {
                        onChecked(e.target.checked)
                    }}>
                    </Form.Check>
                </Form.Group>
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
            <Post content={p.content} created={p.created} creatorId={p.creatorId} imageId={p.imageId}/>)
        )}
    </Container>)

const SearchView = ({users, friends, onSearch, onAddFriend, onRemoveFriend, onViewPosts, onChecked}) => (
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
                                        isFriend={friends.find(f => f.id === u.id)}
                                        onChecked={(checked) => onChecked(u.id, checked)}/>),)
        }
    </Container>)

const People = ({authUser}) => {
        const [displaySearch, setDisplaySearch] = useState(true)
        const [error, setError] = useState(null)

        const [posts, setPosts] = useState([])
        const [, setLoading] = useState(true);

        const [users, setUsers] = useState([])
        let [checkedUsers, setCheckedUsers] = useState([authUser.id])
        const [friends, setFriends] = useState([])
        const [failAlertVisible, setFailAlertVisible] = useState(false)

        const [chatName, setChatName] = useState('')

        const onError = (err) => {
            setFriends([])
            setUsers([])
            setError(err)
            handleAlertVisibility(setFailAlertVisible)
        }

        useEffect(() => {
            fetchFriends(setFriends, authUser.token, onError)
        }, [authUser.token, checkedUsers])

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
            }, setError, setLoading, id)
        }

        const handleChecked = (userId, checked) => {
            if (checked) {
                setCheckedUsers([...checkedUsers, userId])
            } else {
                setCheckedUsers(checkedUsers.filter(id => id !== userId));
            }
        }

        return (
            <>
                <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>
                {displaySearch ?
                    <>
                        <SearchView
                            users={users}
                            friends={friends}
                            onSearch={async e => await fetchUsers(e.target.value, setUsers, authUser.id, onError)}
                            onViewPosts={handleViewPosts}
                            onAddFriend={handleAddFriend}
                            onRemoveFriend={handleRemoveFriend}
                            onChecked={handleChecked}
                        />
                        {
                            checkedUsers.length <= 1 ?
                                <></>
                                :
                                <Container className="d-grid gap-2">
                                    <Form>
                                        <Form.Control type="text" placeholder="Ange namn på gruppchatt" style={central}
                                                      value={chatName}
                                                      onChange={e => {
                                                          const text = e.target.value
                                                          setChatName(text)
                                                      }}
                                        />
                                    </Form>
                                    <Button style={central}
                                            onClick={() => {
                                                createChat(setError, () => {
                                                }, authUser.token, chatName, checkedUsers)
                                            }
                                            }>
                                        Skapa konversation
                                    </Button>
                                </Container>
                        }
                    </> :
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