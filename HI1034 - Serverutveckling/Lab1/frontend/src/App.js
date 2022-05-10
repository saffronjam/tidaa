import Login from './components/Login'
import Feed from './components/Feed'
import {Button, Container, Nav, Navbar} from "react-bootstrap";
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';

import {useNavigate} from 'react-router-dom';

import './App.css';
import {useState} from "react";
import {Link, Route, Routes} from "react-router-dom";
import MyPosts from "./components/MyPosts";
import People from "./components/People";
import Chats from "./components/Chats";
import Profile from "./components/Profile";
import NotFound from "./components/NotFound";
import RequireAuth from "./components/RequiresAuth";


const App = () => {
    const sessionUser = sessionStorage.getItem("authUser");
    const [authUser, setAuthUser] = useState(sessionUser == null ? null : JSON.parse(sessionUser))
    const navigate = useNavigate();

    let setAuthUserStorage = (authUser) => {
        sessionStorage.setItem("authUser", JSON.stringify(authUser))
        setAuthUser(authUser)
    }

    if (!authUser) {
        return (
            <Container className="p-1">
                <Login setAuthUser={setAuthUserStorage}/>
            </Container>
        )
    }

    // More room for preparation if needed.

    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href="/">Ansiktsboken</Navbar.Brand>
                    <Nav className="me-auto">
                        <Nav.Link as={Link} to="/">Flöde</Nav.Link>
                        <Nav.Link as={Link} to="/posts">Mina inlägg</Nav.Link>
                        <Nav.Link as={Link} to="/people">Personer</Nav.Link>
                        <Nav.Link as={Link} to="/chats">Konversationer</Nav.Link>
                    </Nav>
                    <Nav className="ml-auto">
                        <Nav.Link as={Link} to="/profile">Min profil</Nav.Link>
                        <Button onClick={() => {
                            setAuthUserStorage(null)
                            navigate("/")
                        }}>Logga ut</Button>
                    </Nav>
                </Container>
            </Navbar>

            <Routes>
                <Route path="/" element={<Feed authUser={authUser} setAuthUser={setAuthUserStorage}/>}/>
                <Route element={<RequireAuth authUser={authUser}/>}>
                    <Route path="/posts" element={<MyPosts authUser={authUser}/>}/>
                    <Route path="/people" element={<People authUser={authUser}/>}/>
                    <Route path="/profile" element={<Profile authUser={authUser}/>}/>
                    <Route path="/chats" element={<Chats authUser={authUser}/>}/>
                </Route>
                <Route path="*" element={<NotFound/>}/>
            </Routes>
        </>
    )
}

export default App;