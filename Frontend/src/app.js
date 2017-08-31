import "./app.css";
import "jszip"


class App {
    constructor() {
        this._texturesForm = document.getElementById('texturesForm');
        this._files = document.getElementById('files');
        this._addListeners();
    }
    _addListeners() {
        this._texturesForm.addEventListener('submit', this._onSubmitTextures.bind(this));

    }
    _onSubmitTextures(event) {
        event.preventDefault();

        const files = new FormData();

        for (const file of this._files.files) {
            files.append(file.name, file);
        }

        fetch('/api/compress', {
            method: 'POST',
            body: files
        })
    }
}

new App();