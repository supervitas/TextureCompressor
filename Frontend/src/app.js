import './app.css';

class App {
    constructor() {
        this._texturesForm = document.getElementById('texturesForm');
        this._files = document.getElementById('files');
        this._compressBtn = document.getElementById('compressBtn');
        this._linkContainer = document.getElementById('link');
        this._statusContainer = document.getElementById('status');

        this._jobID = null;
        this._addListeners();
    }
    _addListeners() {
        this._texturesForm.addEventListener('submit', this._onSubmitTextures.bind(this));
    }
    _onSubmitTextures(event) {
        event.preventDefault();

        this._clearNode(this._linkContainer);
        this._clearNode(this._statusContainer);

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

                this._updateStatusOfCompressing(processed, allFiles);

                if (isReady) {
                    const path = json.path;
                    this._stopPendingJob();
                    this._createLink(path);
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

    _clearNode(node) {
        while (node.firstChild) {
            node.removeChild(node.firstChild);
        }
    }

    _updateStatusOfCompressing(filesCompressed, filesTotal) {
        this._clearNode(this._statusContainer);

        const t = document.createTextNode(`Compressed: ${filesCompressed} Total: ${filesTotal}`);
        this._statusContainer.appendChild(t);
    }

    _createLink(link) {
        this._clearNode(this._linkContainer);

        const a = document.createElement('a');
        const linkText = document.createTextNode("Result");

        a.appendChild(linkText);
        a.title = "Download Compressed Textures";
        a.href = link;
        this._linkContainer.appendChild(a);
    }
}

new App();