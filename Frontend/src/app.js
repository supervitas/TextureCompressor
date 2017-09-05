import './app.css';

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
        }).then((response) => {
            return response.json().then((json)=> {
                if (!response.ok) {
                    return Promise.reject(json.error);
                }
                return Promise.resolve(json);
            });
        }).then((json) => {
            this._waitForJobDone(json.jobID);
        }).catch((err) => {
            alert(err);
            this._compressBtn.disabled = false;
        });
    }

    _waitForJobDone(jobID) {
        this._jobID = setInterval(() => {
            fetch('/api/status', {
                method: 'POST',
                body : JSON.stringify({jobID})
            }).then((response) => {
                return response.json().then((json)=> {
                    if (!response.ok) {
                        return Promise.reject(json.error);
                    }
                    return Promise.resolve(json);
                });
            }).then((json) => {
                const processed = json.processedFiles;
                const allFiles = json.allFiles;
                const isReady = json.isReady === 'true';

                if (isReady) {
                    const path = json.path;
                    this._stopPendingJob();
                    this._downloadCompressedTextures(path);
                }
            }).catch((err) => {
                alert(err);
                this._stopPendingJob();
            })
        }, 3000)
    }

    _stopPendingJob() {
        this._compressBtn.disabled = false;
        this._files.value = '';
        clearInterval(this._jobID);
    }

    _downloadCompressedTextures(path) {
        console.log(path);
    }
}

new App();