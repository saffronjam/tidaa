import {Form, Button, Container} from "react-bootstrap";
import {fetch} from "whatwg-fetch";
import {useState} from "react";
import {TopAlert, handleAlertVisibility} from "./TopAlert";
import {USER_MS_IP} from "../Constants";

async function loginUser(credentials) {
    return fetch(`${USER_MS_IP}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    }).then(res => {
        if (res.ok) {
            return res.json()
        }
        throw res
    }).then(jsonRes => {
        return ({success: true, authUser: jsonRes})
    }).catch(_ => {
        return ({success: false, error: 'Misslyckade att logga in'})
    })
}

const registerUser = async (username, email, password) => {
    return fetch(`${USER_MS_IP}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            email: email,
            password: password
        })
    }).then(response => {
        if (!response.ok) {
            throw response
        }
        return {success: true}
    }).catch(_ => {
        return {success: false, error: 'Misslyckade att registrera användare'}
    })
}

const handleLoginSubmit = async (event, setAuthUser, setValidated, onError) => {
    event.preventDefault();

    // Start by validating form
    const form = event.currentTarget;
    if (form.checkValidity() === false) {
        setValidated(true);
        return;
    }
    let result = await loginUser({usernameOrEmail: event.target[0].value, password: event.target[1].value});
    if (result.success) {
        setAuthUser(result.authUser);
        setValidated(true);
    } else {
        setValidated(false);
        onError(result.error)
    }
}

const handleRegisterSubmit = async (event, setValidated, onSuccess, onError) => {
    event.preventDefault();

    // Start by validating form
    const form = event.currentTarget;
    if (form.checkValidity() === false) {
        setValidated(true);
        return;
    }
    let result = await registerUser(event.target[0].value, event.target[1].value, event.target[2].value)
    if (result.success) {
        setValidated(true);
        onSuccess()
    } else {
        setValidated(false);
        onError(result.error)
    }
}

const LoginForm = ({validated, onSubmit, onWantCreateUser}) => {
    return (<Form noValidate validated={validated} onSubmit={onSubmit}>
        <Form.Group className="mb-3" controlId="loginUsernameOrEmail">
            <Form.Control name="loginUsernameOrEmail" type="text" placeholder="Användarnamn eller mejl"
                          required/>
        </Form.Group>
        <Form.Group className="mb-3" controlId="loginPassword">
            <Form.Control name="loginPassword" type="password" placeholder="Lösenord" required/>
        </Form.Group>
        <Button variant='primary' type='submit'>
            Logga in
        </Button>
        <Button variant='secondary' onClick={onWantCreateUser}>
            Skapa användare
        </Button>
    </Form>)
}

const RegisterForm = ({validated, onSubmit, onWantLogin}) => {
    return (<Form noValidate validated={validated} onSubmit={onSubmit}>
        <Form.Group className="mb-3" controlId="registerUsername">
            <Form.Control name="registerUsername" type="text" placeholder="Användarnamn"
                          required/>
        </Form.Group>
        <Form.Group className="mb-3" controlId="registerEmail">
            <Form.Control name="registerEmail" type="email" placeholder="Mejl"
                          required/>
        </Form.Group>
        <Form.Group className="mb-3" controlId="registerPassword">
            <Form.Control name="registerPassword" type="password" placeholder="Lösenord" required/>
        </Form.Group>
        <Button variant='primary' type='submit'>
            Skapa
        </Button>
        <Button variant='secondary' onClick={onWantLogin}>
            Gå tillbaka
        </Button>
    </Form>)
}

const Login = ({setAuthUser}) => {
    // For validation
    const [validatedLogin, setValidatedLogin] = useState(false);
    const [validatedRegister, setValidatedRegister] = useState(false);
    const [login, setLogin] = useState(true)
    const [failAlertVisible, setFailAlertVisible] = useState(false)
    const [registerAlertVisible, setRegisterAlertVisible] = useState(false)
    const [info] = useState('Successfully created user')
    const [error, setError] = useState(null)

    const handleWantViewChange = (login) => {
        setLogin(login)
        setValidatedLogin(false)
        setValidatedRegister(false)
    }

    const onError = (err) => {
        setError(err)
        handleAlertVisibility(setFailAlertVisible)
    }

    const onSuccessFulRegister = () => {
        setLogin(true)
        handleAlertVisibility(setRegisterAlertVisible)
    }

    return (
        <>
            <TopAlert variant={'danger'} show={failAlertVisible}>{error}</TopAlert>
            <TopAlert show={registerAlertVisible}>{info}</TopAlert>

            <Container>
                <h1 className="header">Välkommen till Ansiktsboken</h1>
                {(login ?
                        (<LoginForm onSubmit={e => handleLoginSubmit(e, setAuthUser, setValidatedLogin, onError)}
                                    validated={validatedLogin}
                                    onWantCreateUser={() => handleWantViewChange(false)}/>) :
                        (<RegisterForm validated={validatedRegister}
                                       onSubmit={e => handleRegisterSubmit(e, setValidatedRegister, () => onSuccessFulRegister(), onError)}
                                       onWantLogin={() => handleWantViewChange(true)}
                        />)

                )}
            </Container>
        </>
    )
}
export default Login