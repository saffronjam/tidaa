import Canvas from "./Canvas";
import {Container} from "react-bootstrap";


const Draw = ({authUser}) => {
    return (
        <>
            <Container>
                <Canvas chatId={0}/>
            </Container>
        </>
    )
}

export default Draw;