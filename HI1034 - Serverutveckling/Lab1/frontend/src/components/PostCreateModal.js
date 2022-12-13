import {Button, Form, Modal} from "react-bootstrap";
import FileUploader from "./FileUploader";

const PostCreateModal = ({
                             show,
                             onSubmit,
                             onCloseCreatePostModal,
                             validated,
                             setContent,
                             setImage
                         }) => {
    return (
        <Modal show={show} onHide={() => onCloseCreatePostModal()}>
            <Form noValidate validated={validated} onSubmit={onSubmit}>
                <Modal.Body>
                    <Form.Group onChange={e => setContent(e.target.value)} className="mb-3"
                                controlId="exampleForm.ControlTextarea1">
                        <Form.Control style={{
                            border: "none",
                            outline: "none",
                            resize: "none"
                        }} placeholder="Vad gör du just nu?" as="textarea" rows={3} required/>
                    </Form.Group>
                    <FileUploader className='noselect' onSelect={(file) => setImage(file)}>Ladda upp bild</FileUploader>

                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => onCloseCreatePostModal()}>
                        Kasta
                    </Button>
                    <Button variant="primary" type="submit">
                        Skapa
                    </Button>
                </Modal.Footer>
            </Form>

        </Modal>
    )
}

export default PostCreateModal