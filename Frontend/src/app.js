import "./app.css";
import "jszip"

class App {
    constructor() {
        this._texturesForm = document.getElementById('texturesForm');
        this._files = document.getElementById('files');
        this._compressBtn = document.getElementById('compressBtn');
        this._jobID = null;
        this._addListeners();
    }
    _addListeners() {
        this._texturesForm.addEventListener('submit', this._onSubmitTextures.bind(this));

    }
    _onSubmitTextures(event) {
        event.preventDefault();

        this._compressBtn.disabled = true;

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
        this._jobID = setInterval(() => {
            fetch('/api/status', {
                method: 'POST',
                body : JSON.stringify({jobID})
            }).then((response, reject) => {
                if(reject) {
                    throw(reject);
                }
                return response.json();
            }).then((json) => {
                const processed = json.processedFiles;
                const allFiles = json.allFiles;
                const isReady = json.isReady;

                if(isReady) {
                    this._stopPendingJob();
                }
            }).catch((err) => {
                alert(err);
            })
        }, 3000)
    }

    _stopPendingJob() {
        this._compressBtn.disabled = false;
        this._files.value = "";

        clearInterval(this._jobID);
    }

}

new App();