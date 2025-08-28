document.addEventListener('DOMContentLoaded', function() {
	
	// tasks.htmlから各idの要素を取り出し、管理
	const userSettingsModal = document.getElementById('userSettingsModal');
	const userSettingsForm = document.getElementById('userSettingsForm');
	const userSettingsErrMsg = document.getElementById('userSettingsErrMsg');
	
    const editTaskModal = document.getElementById('editTaskModal');
    const addTaskForm = document.getElementById('addTaskForm');
    const addTaskModalElement = document.getElementById('addTaskModal');
    const addTaskErrMsgs = document.getElementById('addTaskErrMsgs');
    const editTaskForm = document.getElementById('editTaskForm');
    const editTaskErrMsgs = document.getElementById('editTaskErrMsgs');

	// モーダル画面はbootstrapの管理下にあるため、エレメントを使用して管理する必要がある
    const addTaskModal = new bootstrap.Modal(addTaskModalElement);
    const editTaskModalInstance = new bootstrap.Modal(editTaskModal);
	const userSettingsModalInstance = new bootstrap.Modal(userSettingsModal);
	
    // 共通の非同期送信・レスポンス処理関数
    const handleFormSubmit = (form, errMsgsDiv, successCallback) => {

		// formに記載された内容を取り出す
		const formData = new FormData(form);
        const url = form.action;

        errMsgsDiv.style.display = 'none';

		// fetchで動的にリクエストを作成
        fetch(url, {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
        .then(response => response.json().then(data => ({ status: response.status, body: data })))
        .then(res => {

			// 成功
            if (res.status === 200) {
                if (res.body.success) {
					// 成功時のメッセージ
                    alert(res.body.message);
                    successCallback();
                }
			// 不正なリクエストの場合
            } else if (res.status === 400) {
                if (res.body.errList && res.body.errList.length > 0) {
                    errMsgsDiv.innerHTML = '<ul>' + res.body.errList.map(msg => `<li>${msg}</li>`).join('') + '</ul>';
					errMsgsDiv.style.display = 'block';
                }
            }
        })
		// 例外発生時
        .catch(err => {
            console.error('Err:', err);
            errMsgsDiv.innerText = '予期せぬエラーが発生しました。';
            errMsgsDiv.style.display = 'block';
        });
    };

    // 新規タスク追加フォームの送信処理
    addTaskForm.addEventListener('submit', function(event) {
        event.preventDefault();
        handleFormSubmit(
			this, 
			addTaskErrMsgs,
			 () => {
            	addTaskModal.hide();
            	window.location.reload();
        	}
		);
    });

    // 更新タスクフォームの送信処理
    editTaskForm.addEventListener('submit', function(event) {
        event.preventDefault();
        handleFormSubmit(
			this, 
			editTaskErrMsgs,
			 () => {
	            editTaskModalInstance.hide();
    	        window.location.reload();
        	}
		);
    });
    
    // 更新モーダル出現時、入力欄に更新前の情報を格納しておく
    editTaskModal.addEventListener('show.bs.modal', function (event) {
        // 更新ボタンに格納しておいた各情報をモーダル画面へ渡す
		const button = event.relatedTarget;
        const taskId = button.getAttribute('data-task-id');
        const taskCategoryId = button.getAttribute('data-task-category');
        const taskTitle = button.getAttribute('data-task-title');
        const taskMemo = button.getAttribute('data-task-memo');
        const taskProgress = button.getAttribute('data-task-progress');
        const taskClosingDate = button.getAttribute('data-task-closingdate');
        
		// formの情報を格納
        const form = document.getElementById('editTaskForm');
        form.action = `/tasks/edit/${taskId}`;
        
        document.getElementById('editCategoryId').value = taskCategoryId;
        document.getElementById('editTitle').value = taskTitle;
        document.getElementById('editMemo').value = taskMemo;
        document.getElementById('editProgress').value = taskProgress;
        document.getElementById('editClosingDate').value = taskClosingDate;

        editTaskErrMsgs.style.display = 'none';
    });
	
	// ユーザー情報更新フォームの送信処理
    userSettingsForm.addEventListener('submit', function(event) {
        event.preventDefault();
        handleFormSubmit(
			this, 
			userSettingsErrMsg,
			 () => {
            	userSettingsModalInstance.hide();
            	window.location.reload();
        	}
		);
    });
	
	
});
