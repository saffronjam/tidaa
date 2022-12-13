import {useEffect} from "react";

const FileUploader = ({onSelect, disabled, value}) => {
    const handleFileInput = e => {
        const file = e.target.files[0];
        e.target.value = null
        onSelect(file);
    }

    useEffect(() => {
    }, [])


    return (
        <>
            <label htmlFor={'image'}>
                <input type={'file'}
                       name={'image'}
                       id={'image'}
                       style={{display: 'none'}}
                       onChange={handleFileInput}/>
                <img src={'https://www.rubberstamps.net/Images/6-free-image-upload.svg'}
                     alt={'upload-button'}
                     style={{
                         height: "3rem"
                     }}/>
            </label>
        </>
    )
}

export default FileUploader