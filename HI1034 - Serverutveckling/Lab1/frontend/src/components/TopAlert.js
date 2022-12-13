import {Alert, Fade} from "react-bootstrap";

const handleAlertVisibility = (setVisible, timeout = 2000) => {
    setVisible(true)
    setTimeout(() => {
        setVisible(false)
    }, timeout);
}

const TopAlert = (props) => {
    return (
        <Alert show={props.show} transition={Fade} style={{
            position: "absolute",
            margin: "auto",
            top: "1em",
            left: "0",
            right: "0",
            width: "50%",
            textAlign: "center",
            zIndex: 9999,
            borderRadius: "5px"
        }} variant={props.variant}>
            {props.children}
        </Alert>
    )
}

export {TopAlert, handleAlertVisibility}