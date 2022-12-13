import {fetch} from "whatwg-fetch";
import {useEffect, useState} from "react";
import {Button, Container} from "react-bootstrap";
import '../App.css';
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import PostCreateModal from "./PostCreateModal";
import {Post, fetchPosts} from "./Post";
import {POST_MS_IP} from "../Constants";
import {fetchUserLogins} from "./Reports";

const sendPost = async (image, content, token, reports) => {

    let formData = new FormData();
    if (image) {
        formData.append('image', image)
    }
    formData.append('content', content)
    formData.append('token', token)
    formData.append('reportsString', JSON.stringify(reports))

    return await fetch(`${POST_MS_IP}/posts`, {
        method: "POST",
        body: formData
    }).then(response => {
        if (response.ok) {
            return ({success: true})
        }
        throw response
    }).catch(err => {
        return ({success: false, error: err})
    })
}

const MyPosts = ({authUser}) => {
    // Load posts
    const [posts, setPosts] = useState([])
    const [, setLoading] = useState(true);
    const [error, setError] = useState(null)

    // Create post
    const [showCreatePost, setShowCreatePost] = useState(false);
    const [validated, setValidated] = useState(false)
    const [failAlertVisible, setFailAlertVisible] = useState(false)

    // Fields
    const [content, setContent] = useState('')
    const [image, setImage] = useState(null)
    const [wantReports, setWantReports] = useState(false)

    const onError = (error) => {
        handleAlertVisibility(setFailAlertVisible);
        setError(error)
    }

    useEffect(() => {
        fetchPosts(setPosts, setError, setLoading, authUser.id)
    }, [authUser.id])

    const onCloseCreatePostModal = () => {
        setShowCreatePost(false)
        setValidated(false)
        setImage(null)
    }

    const handleSubmit = async (event) => {

        event.preventDefault();

        // Start by validating form
        const form = event.currentTarget;
        if (form.checkValidity() === false) {
            setValidated(true);
            return;
        }

        if (content == null || content.length === 0) {
            setValidated(true);
        }

        let result;
        let reports = []
        if (wantReports) {
            reports = await fetchUserLogins(authUser.id)
                .then(jsonData => jsonData)
                .catch(_ => [])
        }

        // Send to Api
        result = await sendPost(image, content, authUser.token, reports);

        if (result.success) {
            setValidated(false)
            setShowCreatePost(false)
            setPosts([])
            fetchPosts(setPosts, onError, setLoading, authUser.id)
        } else {
            handleAlertVisibility(setFailAlertVisible)
            setValidated(true)
        }

        setWantReports(false)
        setImage(null)
    }

    return (
        <>
            <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>

            <Container className="d-grid gap-2">
                <Button style={{
                    width: "25rem",
                    marginRight: "auto",
                    marginLeft: "auto",
                    marginTop: "1rem"
                }} variant="primary" onClick={() => setShowCreatePost(true)}>
                    Vad gör du just nu, {authUser.name}?
                </Button>

                <TopAlert show={failAlertVisible} variant='danger'>Failed to create post</TopAlert>

                <PostCreateModal onSubmit={(e) => handleSubmit(e)}
                                 onCloseCreatePostModal={onCloseCreatePostModal}
                                 show={showCreatePost}
                                 validated={validated}
                                 setContent={setContent}
                                 setImage={setImage}
                                 setWantReports={setWantReports}
                />


                {posts.map(p => {
                    return <Post key={p.id}
                                 content={p.content}
                                 imageId={p.imageId}
                                 creatorId={p.creatorId}
                                 created={p.created}
                                 reportsId={p.reportsId}/>;
                })}
            </Container>
        </>
    );
};

export default MyPosts