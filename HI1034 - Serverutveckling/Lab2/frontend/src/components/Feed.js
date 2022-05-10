import {useEffect, useState} from "react";
import {Post, fetchPosts} from "./Post";
import {fetch} from "whatwg-fetch";
import {USER_MS_IP} from "../Constants";

const fetchFriends = (token, setFriends, setError) => {
    fetch(`${USER_MS_IP}/users/${token}/friends`, {
        method: "GET",
        headers: "application/json"
    })
        .then(res => {
            if (res.ok) {
                return res.json()
            }
            throw res
        })
        .then(res => setFriends(res))
        .catch(_ => {
            setError("Failed to fetch friends")
            return []
        })
}

let friends = []

const Feed = ({authUser}) => {
    const [posts, setPosts] = useState([])

    const [, setLoading] = useState(true);
    const [, setError] = useState(null)

    const setFriendsThenPosts = (newFriends) => {
        friends = newFriends
        fetchPosts(setPosts, setError, setLoading, -1, newFriends.map(e => e.id))
    }


    useEffect(() => {
        let intervalId = setInterval(() => {
            fetchPosts(setPosts, setError, setLoading, -1, friends.map(e => e.id))
        }, 1000);
        fetchFriends(authUser.token, setFriendsThenPosts, setError)

        return () => {
            clearInterval(intervalId)
        }
    }, [authUser.token])
    return (
        <>
            {posts.map(p => {
                return (<Post key={p.id}
                              content={p.content}
                              created={p.created}
                              creatorId={p.creatorId}
                              imageId={p.imageId}
                              reportsId={p.reportsId}/>)
            })}
        </>
    );
};

export default Feed