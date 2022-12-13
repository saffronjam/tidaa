import {Form} from "react-bootstrap";

const FileUploader = (props) => {
    const handleFileInput = (e) => {
        const file = e.target.files[0];
        props.onSelect(file);
    }

    return (
        <Form.Group onChange={handleFileInput}>
            <Form.Label>{props.children}</Form.Label>
            <Form.Control type="file"/>
        </Form.Group>
    )
}

export default FileUploader