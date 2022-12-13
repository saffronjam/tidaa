import {render} from '@testing-library/react';
import App from './App';
import {BrowserRouter} from "react-router-dom";

test('Render Everything', () => {
    render(
        <BrowserRouter>
            <App/>
        </BrowserRouter>
    );
});
