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
        }).then((response, reject) => {
            if (reject) {
               alert(reject);
               return
            }
            return response.json();
        }).then((json) => {
            this._waitForJobDone(json.jobID);
        });
    }

    _waitForJobDone(jobID) {
        const id = setInterval(() => {
            fetch('/api/status', {
                method: 'POST',
                body : JSON.stringify({jobID})
            }).then((response, reject) => {
                if(reject) {
                    throw(reject);
                }
                return response.json();
            }).then((json) => {
                console.log(json);
            }).catch((err) => {
                alert(err);
            })
        }, 3000)
    }

}

new App();