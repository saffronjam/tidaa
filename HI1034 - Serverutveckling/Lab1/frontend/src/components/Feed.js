import {useEffect, useState} from "react";
import {Post, fetchPosts} from "./Post";

const Feed = ({authUser}) => {
    const [posts, setPosts] = useState([])
    const [, setLoading] = useState(true);
    const [, setError] = useState(null)

    useEffect(() => {
        let intervalId = setInterval(() => {
            fetchPosts(setPosts, setError, setLoading, authUser.token)
        }, 1000);
        fetchPosts(setPosts, setError, setLoading, authUser.token)
        return () => {
            clearInterval(intervalId)
        }
    }, [authUser.token])

    return (
        <>
            {posts.map(p => {
                return (<Post content={p.content} created={p.created} creator={p.creator} imageId={p.imageId}/>)
            })}
        </>
    );
};

export default Feed