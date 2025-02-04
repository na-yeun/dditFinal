/**
 * 
 */

let sameFilesCheck = [];

document.addEventListener("DOMContentLoaded", ()=>{
    const allCheck = document.getElementById('allCheck');
	const fileInput = document.getElementById("fileInput");
	const downloadBtn = document.getElementById("downloadBtn");
	const makefolderBtn = document.getElementById("makefolderBtn");
 	const deleteBtn = document.getElementById("deleteBtn");
    const modalElement = document.getElementById('smallModal');
    const searchBtn = document.getElementById("searchBtn");
    const fileMoveBtn = document.getElementById("fileMoveBtn");
    const pasteBtn = document.getElementById("pasteBtn");
    const currentPathForm = document.getElementById("currentPathForm");

    // .file-name 클래스를 가진 모든 요소를 선택하여 배열에 추가
    const fileElements = document.querySelectorAll(".file-name");
    fileElements.forEach(fileElement => {
        sameFilesCheck.push({name : fileElement.textContent.trim()}); // 파일 이름 저장
    });

    currentPathForm.value = "PublicCloud:/"+path;

    // Bootstrap Modal 인스턴스 생성
    const modalInstance = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);


    // 스크롤 바 생성.
    new PerfectScrollbar(document.getElementById('vertical-example'), {
        wheelPropagation: false
      });

    var moveSelected = [];
    var targetNames = [];

    fileMoveBtn.addEventListener("click", () => {
        
        targetNames = [];

        var selectedFolders = document.querySelectorAll('input[name="folder"]:checked');

        var selectedFiles = document.querySelectorAll('input[name="file"]:checked');

        if(selectedFiles.length === 0){
            Swal.fire({
                title: "이동 실패",
                html: "파일이 선택되지 않았습니다.<br/>파일을 선택 후 다시 시도해주세요.",
                icon: "error"
            });
        }else if(selectedFolders.length === 0 && selectedFiles.length !== 0){
            fileMoveBtn.style.display = "none"; // fileMoveBtn 숨기기
            pasteBtn.style.display = "block";   // pasteBtn 보이기

            selectedFiles.forEach(function(checkbox){
                moveSelected.push((path).trim()+checkbox.value);
                targetNames.push(checkbox.value);
            });

            Swal.fire({
                title: "파일 선택",
                html: `파일이 성공적으로 선택되었습니다.<br>이동하고 싶은 폴더로 이동 후에<br/> <strong>\"이동하기\"</strong> 버튼을 클릭해주세요.`,
                icon: "success",
                showConfirmButton : true
            });// 성공 메시지 표시

        }else{
            Swal.fire({
                title: "이동 실패",
                html: "폴더는 이동할 수 없습니다.<br/> 다시 시도해주세요.",
                icon: "error"
            });
        }

       
    });
    
    pasteBtn.addEventListener("click", async () => {
        pasteBtn.style.display = "none";    // pasteBtn 숨기기
        fileMoveBtn.style.display = "block"; // fileMoveBtn 보이기
    
        const targetPaths = []; // 이동할 대상 경로를 저장
        const validMoveSelected = []; // 유효한 원본 경로 리스트
    
        // if (!sameFilesCheck || sameFilesCheck.length === 0) {
        //     Swal.fire({
        //         title: "오류",
        //         text: "대상 폴더 데이터를 불러오지 못했습니다. 다시 시도해주세요.",
        //         icon: "error",
        //     });
        //     return;
        // }

        // 중복 체크 로직
        for (let i = 0; i < targetNames.length; i++) {
            const name = targetNames[i];
            const originalPath = moveSelected[i]; // 각 파일의 원본 경로와 매핑
            let isDuplicate = false;
    
            for (const file of sameFilesCheck) {
                if (file.name.trim() === name.trim()) { // 공백 제거 후 비교
                    isDuplicate = true;
    
                    const result = await Swal.fire({
                        title: "중복 파일 발견",
                        html: `같은 이름의 파일 <strong>${file.name}</strong>이(가) 존재합니다.<br> 덮어쓸지 여부를 선택해주세요.`,
                        icon: "warning",
                        showCancelButton: true,
                        confirmButtonText: "덮어쓰기",
                        cancelButtonText: "취소",
                    });
    
                    if (result.isConfirmed) {
                        targetPaths.push(path + name); // 덮어쓰기를 선택한 파일만 추가
                        validMoveSelected.push(originalPath); // 원본 경로도 추가
                    }
                    break;
                }
            }
    
            if (!isDuplicate) {
                targetPaths.push(path + name); // 중복되지 않은 파일은 항상 추가
                validMoveSelected.push(originalPath); // 원본 경로도 추가
            }
        }
    
        if (targetPaths.length === 0) {
            Swal.fire({
                title: "파일 이동 취소",
                text: "중복된 파일이 모두 이동에서 제외되었습니다.",
                icon: "info",
            });
            return;
        }
    
        // 요청 데이터 생성
        const payload = {
            moveSelected: validMoveSelected, // 유효한 원본 경로 리스트
            targetPaths: targetPaths         // 대상 경로 리스트
        };
        
        Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});

        // 서버로 이동 요청
        fetch(`${contextPath}/${companyId}/pubCloud/moveObjects`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload) // JSON 형식으로 변환
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("파일 이동 실패");
                }
                return response.json();
            })
            .then(data => {
                Swal.fire({
                    title: "성공",
                    text: "파일이 성공적으로 이동되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: false
                });
    
                // 성공 시 페이지 새로고침 또는 폴더 이동
                if (path === "") {
                    location.reload();
                } else {
                    navigateToFolder('');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    title: "오류",
                    text: "파일 이동에 실패했습니다. 다시 시도해주세요.",
                    icon: "error",
                });
            });
    });

    searchBtn.addEventListener("click", (e) => {
        const searchKeyWord = document.getElementById("searchKeyWord").value.trim();


        // 저장된 경로 초기화
        path = "";

        if (searchKeyWord.length === 0) {
            Swal.fire({
                title: "검색 실패",
                html: "검색어엔 공백이 들어갈 수 없습니다.<br>검색어를 입력 후 다시 시도해주세요.",
                icon: "error"
            });
        }else{
            Swal.fire({
                title: "처리 중입니다...",
                html: "잠시만 기다려주세요.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading(); // 로딩 애니메이션 표시
                },
            });

            // 서버에 검색 요청
            fetch(`${contextPath}/${companyId}/pubCloud/searchObjects`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `searchKeyWord=${encodeURIComponent(searchKeyWord)}`
            })
            .then(response => {
                if (!response.ok) {
                    Swal.fire({
                        title: "검색 실패",
                        text: "검색 중 오류가 발생했습니다. 다시 시도해주세요.",
                        icon: "error"
                    });
                    throw new Error("서버 응답 오류");
                }
                return response.json();
            })
            .then(data => {
                updateSearchTable(data.items); // 검색 결과를 테이블에 표시
                Swal.close();               
            })
            .catch(error => {
                console.error("Error:", error);
                Swal.fire({
                    title: "검색 실패",
                    text: "검색 중 오류가 발생했습니다. 다시 시도해주세요.",
                    icon: "error"
                });
            });
        }
    });

    // 검색 결과를 테이블에 업데이트하는 함수
    function updateSearchTable(items) {
        const tableBody = document.querySelector("#cloudStorage tbody");
        tableBody.innerHTML = ""; // 기존 테이블 내용 비우기
        
        Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});

        if (!items || items.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">검색 결과가 없습니다.</td>
                </tr>`;
            return;
        }
    
        items.forEach(item => {
            var imgTag = fileTypeImgUrl(item.name);
            const row = `
                <tr>
                    <td><input type="checkbox" name="${item.type.toLowerCase()}" value="${item.name}"></td>
                    <td>
                        ${item.type === "Folder" ? `<img id="folderIcon" src="${contextPath}/resources/images/icon/icon-folder.png"></img>` :
                             `<img id="fileIcon" src="${imgTag}"></img>`} 
                    </td>
                    <td>
                        <span ${item.type === "Folder" ? `class="folder-name" onclick="folderThumbnail('${item.path}','${item.name}')" ondblclick="navigateToFolder('${item.path}')"` : `class="file-name" onclick="thumbnail('${item.path}')" id="object"`}>
                            ${item.name}
                        </span>
                    </td>
                    <td>${item.size}</td>
                    <td>${item.lastModified}</td>
                </tr>`;
            tableBody.insertAdjacentHTML("beforeend", row);
        });
    }
    
    

    deleteBtn.addEventListener("click", async (e)=>{
        var selected = [];

		var selectedFolders = document.querySelectorAll('input[name="folder"]:checked');
		
        let filterAns = await deleteCheckboxFilter(selectedFolders);

        if(filterAns == true){
           var selectedFiles = document.querySelectorAll('input[name="file"]:checked');

           selectedFolders.forEach(function(checkbox){
                selected.push((path).trim()+checkbox.value+"/");
           })

           selectedFiles.forEach(function(checkbox){
               selected.push((path).trim()+checkbox.value);
           });
           Swal.fire({
                title: "처리 중입니다...",
                html: "잠시만 기다려주세요.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading(); // 로딩 애니메이션 표시
                },
            });
            
           fetch(`${contextPath}/${companyId}/pubCloud/deleteObjects`, {
                method : "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(selected)
           })
           .then(resp=>{
                if(resp.ok){
                    Swal.fire({
                        title: "파일 삭제",
                        text: "파일이 성공적으로 삭제되었습니다.",
                        icon: "success",
                        timer:2000,
                        showConfirmButton : false
                    });// 성공 메시지 표시
        
                    // 성공 시 페이지 새로고침 또는 다른 작업 수행
                    setTimeout(() => {
                        if(path === ""){
                            location.reload();
                        }else{
                            navigateToFolder('');
                        }
                    }, 2000);
                }else{
                    Swal.fire({
                        title: "파일 삭제",
                        html: "파일을 삭제하던 중 문제가 발생했습니다.<br>해당 오류가 지속적으로 발생할 시<br> 담당자에게 문의 부탁드립니다.",
                        icon: "error"
                    });

                }
           })
           .catch(error =>{
                Swal.fire({
                    title: "파일 삭제 실패",
                    html: `파일을 삭제하던 중 문제가 발생했습니다.<br>해당 현상이 지속적으로 발생할 시<br> 담당자에게 문의 부탁드립니다.`,
                    icon: "error"
                });
           })

        }else{
            Swal.fire({
                title: "파일 삭제 실패",
                html: `파일을 삭제하던 중 문제가 발생했습니다.<br>해당 현상이 지속적으로 발생할 시<br> 담당자에게 문의 부탁드립니다.`,
                icon: "error"
            });
            return;
        }
    });
    
    function deleteCheckboxFilter(selectedFolders) {
    if (selectedFolders.length !== 0) {
        return Swal.fire({
            title: "폴더 삭제",
            html: `선택된 항목 중 폴더가 선택되어있습니다.<br> 해당 폴더를 삭제하시겠습니까?<br>삭제시, 폴더 내부의 모든 정보가 사라집니다.`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: '승인',
            cancelButtonText: '취소',
            confirmButtonColor: 'blue',
            cancelButtonColor: 'red',
            reverseButtons: true,
        }).then(result => {
            if (result.isConfirmed) {
                return true;
            } else {
                Swal.fire("취소 완료", "취소 버튼을 눌렀습니다.", "error");
                return false;
            }
        });
    } else {
        return Promise.resolve(true); // 폴더가 선택되지 않으면 바로 true 반환
    }
}

   
    makefolderBtn.addEventListener("click", (e)=>{
        const folderName = document.getElementById("folderName").value;
        
        Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});

        let answer = spaceFilter(folderName);
        
        const dirName = (path+folderName).trim();
        
        if(answer == true){
             // 모달 닫기
            modalInstance.hide();

            fetch(`${contextPath}/${companyId}/pubCloud/makeDir`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `dirName=${encodeURIComponent(dirName)}`
            })
            .then(response => {
                if (!response.ok) {
                    Swal.fire({
                        title: "새 폴더 생성하기",
                        text: "클라우드에서 새로운 폴더를 생성던 중 문제가 발생했습니다.",
                        icon: "error"
                    });
                    document.getElementById("folderName").value = "";
                    return;
                }else{
                    Swal.fire({
                        title: "새 폴더 생성하기",
                        text: "새로운 폴더가 정상적으로 생성되었습니다.",
                        icon: "success",
                        timer:2000,
                        showConfirmButton : false
                    });// 성공 메시지 표시
                    document.getElementById("folderName").value = "";
                    // 성공 시 페이지 새로고침 또는 다른 작업 수행
                    setTimeout(() => {
                        if(path === ""){
                            location.reload();
                        }else{
                            navigateToFolder('');
                        }
                    }, 2000);
                }
            })
            .catch(error => {
                alert(error.message);
            });
        }else{
            Swal.fire({
                title: "새 폴더 생성하기",
                html: "클라우드에서 새로운 폴더를 생성던 중 문제가 발생했습니다.<br>해당 기능에 오류가 반복적으로 발생한다면 문의 부탁드립니다. ",
                icon: "error"
            });
        }
    });

    function spaceFilter(folderName){
		 // 폴더명을 기입 안했을때.
         if (folderName.length === 0) {
            Swal.fire({
                title: "폴더 생성 실패",
                html: `폴더를 생성할 수 없습니다. <br>생성하실 폴더명을 재입력 후 다시 요청해주세요.`,
                icon: "error"
            });
            return false;
        }
    
        // whitespace - 공백 체크
        if (/\s/.test(folderName)) {
            Swal.fire({
                title: "폴더 생성 실패",
                html: `폴더명을 입력할 때 띄어쓰기를 포함할 수 없습니다. <br>다시 입력해주세요.`,
                icon: "error"
            });
            return false;
        }
    
        // All validations passed
        return true;
    }



	downloadBtn.addEventListener("click", (e)=>{
		var selected = [];
		
        Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});

		var selectedFolders = document.querySelectorAll('input[name="folder"]:checked');
		
        let filterAns = downLoadCheckboxFilter(selectedFolders);
        
        if(filterAns == true){
           var selectedFiles = document.querySelectorAll('input[name="file"]:checked');
           
           selectedFiles.forEach(function(checkbox){
               selected.push((currentPath+path).trim()+checkbox.value);
           });
        }
            // fetch로 데이터 전송
    if (selected.length > 1) {
        fetch(`${contextPath}/${companyId}/pubCloud/downloadMultiple`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                files: selected
            })
        })
        .then(response => {
            if (response.ok) {
                return response.blob();
            } else {
                throw new Error("Download failed");
            }
        })
        .then(blob => {
            // ZIP 파일 다운로드 처리
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "공용클라우드.zip"; // 다운로드 파일 이름
            document.body.appendChild(a);
            a.click();
            a.remove();

            // 로딩 팝업 닫기
            Swal.close();
        })
        .catch(error => {
            Swal.fire({
                title: "다운로드 실패",
                html: "파일을 다운로드가 되지 않았습니다.<br> 해당 기능이 동작하지 않는경우 문의글을 작성해주세요.",
                icon: "error"
            })
        });
    } else if(selected.length == 1){
        fetch(`${contextPath}/${companyId}/pubCloud/downloadSingle`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                file: selected[0]
            })
        })
        .then(resp => {
            if (resp.ok) {
                return resp.blob();
            } else {
                throw new Error("Download failed");
            }
        })
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
    
            // 파일 이름 설정
            const fileName = selected[0].split("/").pop();
            a.download = fileName;
            
            document.body.appendChild(a);
            a.click();
            a.remove();

            // 로딩 팝업 닫기
            Swal.close();
        })
        .catch(error => {
            Swal.fire({
                title: "다운로드 실패",
                html: "파일 다운로드 중 문제가 발생했습니다.<br>문의글을 작성해주세요.",
                icon: "error"
            });
            console.error("Error:", error);
        });
    
    }else{
        Swal.fire({
	        title: "다운로드 실패",
	        html: `폴더는 다운로드할 수 없습니다. <br>다운로드할 파일을 선택 후 다시 요청해주세요.`,
	        icon: "error"
   		});
    }
		
	});
	
	function downLoadCheckboxFilter(selectedFolders){
		if(selectedFolders.length !== 0){
			Swal.fire({
                title: "다운로드 실패",
                html: `폴더는 다운로드할 수 없습니다. <br>다운로드할 파일을 선택 후 다시 요청해주세요.`,

                icon: "error"
           	});
            return false;
		}else{
            return true;
        }
	}
	
    fileInput.addEventListener("change", async () => {
        const files = fileInput.files;
        if (files.length === 0) {
            Swal.fire({
                title: "파일 선택",
                html: "파일이 선택되지 않았습니다.<br> 다운로드할 파일을 선택 후 다시 요청해주세요.",
                icon: "error"
            });
            return;
        }
    
        const validFiles = []; // 업로드할 유효한 파일만 저장
        let duplicateFound = false; // 중복 여부를 추적
    
        // 중복 체크 로직
        for (const file of files) {
            let isDuplicate = false;
    
            for (const same of sameFilesCheck) {
                if (file.name.trim() === same.name.trim()) { // 공백 제거 후 비교
                    isDuplicate = true;
                    duplicateFound = true;
    
                    const result = await Swal.fire({
                        title: "중복 파일 발견",
                        html: `같은 이름의 파일 <strong>${file.name}</strong>이(가) 존재합니다.<br> 덮어쓸지 여부를 선택해주세요.`,
                        icon: "warning",
                        showCancelButton: true,
                        confirmButtonText: "덮어쓰기",
                        cancelButtonText: "취소",
                    });
    
                    if (result.isConfirmed) {
                        validFiles.push(file); // 덮어쓰기를 선택한 파일만 유효 파일로 추가
                    }
                    break;
                }
            }
    
            if (!isDuplicate) {
                validFiles.push(file); // 중복되지 않은 파일은 항상 유효
            }
        }
    
        if (validFiles.length === 0) {
            Swal.fire({
                title: "업로드 취소",
                text: "중복된 파일이 모두 업로드에서 제외되었습니다.",
                icon: "info"
            });
            document.getElementById('fileInput').value = '';
            return;
        }
    
        // 중복 체크 완료 후 유효한 파일 업로드 처리
        const formData = new FormData();
        validFiles.forEach(file => formData.append("files", file)); // 유효한 파일만 추가
        formData.append("folderName", path); // 폴더 이름 추가
        
        Swal.fire({
			title: "처리 중입니다...",
			html: "잠시만 기다려주세요.",
			allowOutsideClick: false,
			didOpen: () => {
				Swal.showLoading(); // 로딩 애니메이션 표시
			},
		});

        fetch(`${contextPath}/${companyId}/pubCloud/upload`, {
            method: "POST",
            body: formData,
        })
        .then(resp => {
            if (resp.ok) {
                Swal.fire({
                    title: "업로드 성공",
                    text: "클라우드에 업로드가 완료되었습니다.",
                    icon: "success",
                    timer: 2000,
                    showConfirmButton: false
                });

                // 성공 시 페이지 새로고침 또는 폴더 이동
                setTimeout(() => {
                    if (path === "") {
                        location.reload();
                    } else {
                        navigateToFolder('');
                        document.getElementById('fileInput').value = '';
                    }
                }, 2000);
            } else {
                Swal.fire({
                    title: "업로드 실패",
                    text: "업로드 중 문제가 발생했습니다.",
                    icon: "error"
                });
            }
        })
        .catch(error => {
            console.error(error);
        });
    });
	
	// allCheck 체크박스 클릭 이벤트
	allCheck.addEventListener('change', (e) => {
        const allChecked = e.target.checked; // 현재 allCheck의 체크 상태를 가져옴
	    const checkboxes = document.querySelectorAll('#cloudStorage tbody input[type="checkbox"]'); // 테이블 내 체크박스 선택
	
	    // 모든 체크박스의 상태를 allCheck의 상태와 동일하게 설정
	    checkboxes.forEach(checkbox => {
	        checkbox.checked = allChecked;
	    });
	});

    // allCheck가 아닌 개별 체크박스 클릭 시 이벤트 처리
    document.addEventListener('change', (e) => {
        if (e.target.matches('#cloudStorage tbody input[type="checkbox"]')) {
            const allCheckboxes = document.querySelectorAll('#cloudStorage tbody input[type="checkbox"]');
            const checkedCheckboxes = document.querySelectorAll('#cloudStorage tbody input[type="checkbox"]:checked');
            const allCheck = document.getElementById('allCheck');

            // 모든 체크박스가 선택되었는지 확인
            allCheck.checked = allCheckboxes.length === checkedCheckboxes.length;
        }
    });

});// DOMContentLoaded
function navigateToFolder(folderName){
	path = `${path}${folderName}`;


    currentPathForm.value = "PublicCloud:/"+path;
    
    if(!path.endsWith("/")){
        path += "/";
    }
	fetch(`${contextPath}/${companyId}/pubCloud`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `path=${encodeURIComponent(path)}`
    })
    .then(resp => {
        if (!resp.ok) {
            Swal.fire({
                title: "폴더 불러오기",
                text: "클라우드에서 해당 폴더를 불러오던 중 문제가 발생했습니다.",
                icon: "error"
            });
        }
        return resp.json(); // JSON 데이터로 변환
    })
    .then(data => {
        sameFilesCheck = data.files || [];
        updateTable(data.folders, data.files); // 테이블 업데이트
    })
    .catch(error => {
        console.error(error);
        Swal.fire({
            title: "오류",
            text: "폴더 데이터를 불러오는 중 문제가 발생했습니다.",
            icon: "error",
        });
    });
}

function updateTable(folders, files) {
    const tableBody = document.querySelector("#cloudStorage tbody");
    tableBody.innerHTML = ""; // 기존 테이블 내용 비우기
    document.getElementById('allCheck').checked = false;
    
    folders = folders || [];
    files = files || [];

     // 상위 폴더로 가기 버튼 추가 (최상위 폴더가 아닐 경우만 표시)
     if (path !== "") {
        const parentRow = `
            <tr>
                <td></td>
                <td>
                    <img id="folderIcon" src="${contextPath}/resources/images/icon/icon-folder.png"></img>
                </td>
                <td>
                    <span style="cursor: pointer; font-weight: bold;" onclick="navigateToParentFolder()">.. (상위 폴더로 이동)</span>
                </td>
                <td></td>
                <td></td>
            </tr>
        `;
        tableBody.insertAdjacentHTML("beforeend", parentRow);
    }

    // 폴더 데이터 추가
    folders.forEach(folder => {
        const row = `
             <tr>
            	<td>
            		<input type="checkbox" name="folder" value="${folder.name}">
            	</td>
                <td>
                    <img id="folderIcon" src="${contextPath}/resources/images/icon/icon-folder.png"></img>
                </td>
                <td>
                	<span class="folder-name" onclick="folderThumbnail('','${folder.name}')" ondblclick="navigateToFolder('${folder.name}')">${folder.name}</span>
                </td>
                <td>${folder.size}</td>
                <td>${folder.lastModified}</td>
            </tr>`;
        tableBody.insertAdjacentHTML("beforeend", row);
    });

    // 파일 데이터 추가
    files.forEach(file => {

        var imgTag = fileTypeImgUrl(file.name);
        const row = `
            <tr>
				<td>
            		<input type="checkbox" name="file" value="${file.name}">
            	</td>
                <td>
                    <img id="fileIcon" src="${imgTag}"></img>
                </td>
                <td>
                <span class="file-name" onclick="thumbnail('${file.name}')" >${file.name}</span>
                </td>
                <td>${file.size}</td>
                <td>${file.lastModified}</td>
            </tr>`;
        tableBody.insertAdjacentHTML("beforeend", row);
    });
}

function fileTypeImgUrl(fileName){
    if(fileName.endsWith(".css")){
        return `${contextPath}/resources/images/icon/icon-css.png`;
    }else if(fileName.endsWith(".doc")){
        return `${contextPath}/resources/images/icon/icon-doc.png`;
    }else if(fileName.endsWith(".html")){
        return `${contextPath}/resources/images/icon/icon-html.png`;
    }else if(fileName.endsWith(".gif")){
        return `${contextPath}/resources/images/icon/icon-gif.png`;
    }else if(fileName.endsWith(".jar")){
        return `${contextPath}/resources/images/icon/icon-jar.png`;
    }else if(fileName.endsWith(".java")){
        return `${contextPath}/resources/images/icon/icon-java.png`;
    }else if(fileName.endsWith(".jpeg")){
        return `${contextPath}/resources/images/icon/icon-jpeg.png`;
    }else if(fileName.endsWith(".jpg")){
        return `${contextPath}/resources/images/icon/icon-jpg.png`;
    }else if(fileName.endsWith(".js")){
        return `${contextPath}/resources/images/icon/icon-js.png`;
    }else if(fileName.endsWith(".mp4")){
        return `${contextPath}/resources/images/icon/icon-mp4.png`;
    }else if(fileName.endsWith(".odt")){
        return `${contextPath}/resources/images/icon/icon-odt.png`;
    }else if(fileName.endsWith(".pdf")){
        return `${contextPath}/resources/images/icon/icon-pdf.png`;
    }else if(fileName.endsWith(".php")){
        return `${contextPath}/resources/images/icon/icon-php.png`;
    }else if(fileName.endsWith(".png")){
        return `${contextPath}/resources/images/icon/icon-png.png`;
    }else if(fileName.endsWith(".pptx") || fileName.endsWith(".ppt")){
        return `${contextPath}/resources/images/icon/icon-ppt.png`;
    }else if(fileName.endsWith(".txt")){
        return `${contextPath}/resources/images/icon/icon-txt.png`;
    }else if(fileName.endsWith(".xls")){
        return `${contextPath}/resources/images/icon/icon-xls.png`;
    }else if(fileName.endsWith(".xlsx")){
        return `${contextPath}/resources/images/icon/icon-xlsx.png`;
    }else if(fileName.endsWith(".xml")){
        return `${contextPath}/resources/images/icon/icon-xml.png`;
    }else if(fileName.endsWith(".zip")){
        return `${contextPath}/resources/images/icon/icon-zip.png`;
    }else{
        return `${contextPath}/resources/images/icon/icon-file.png`;
    }
}

function thumbnail(object){
    const thumbnailName = (path+object).trim();
    fetch(`${contextPath}/${companyId}/pubCloud/imageThumbnail`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `filePath=${thumbnailName}`
    })
    .then(response => response.json())
    .then(data => {
        const thumbnail = document.getElementById("thumbnail");
        thumbnail.innerHTML = "";

            if(data.fileName.endsWith(".css")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-css.png`;
            }else if(data.fileName.endsWith(".doc")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-doc.png`;
            }else if(data.fileName.endsWith(".html")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-html.png`;
            }else if(data.fileName.endsWith(".jar")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-jar.png`;
            }else if(data.fileName.endsWith(".java")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-java.png`;
            }else if(data.fileName.endsWith(".js")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-js.png`;
            }else if(data.fileName.endsWith(".mp4")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-mp4.png`;
            }else if(data.fileName.endsWith(".odt")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-odt.png`;
            }else if(data.fileName.endsWith(".pdf")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-pdf.png`;
            }else if(data.fileName.endsWith(".php")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-php.png`;
            }else if(data.fileName.endsWith(".pptx") || data.fileName.endsWith(".ppt")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-ppt.png`;
            }else if(data.fileName.endsWith(".txt")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-txt.png`;
            }else if(data.fileName.endsWith(".xls")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-xls.png`;
            }else if(data.fileName.endsWith(".xlsx")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-xlsx.png`;
            }else if(data.fileName.endsWith(".xml")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-xml.png`;
            }else if(data.fileName.endsWith(".zip")){
                data.imageUrl = `${contextPath}/resources/images/icon/icon-zip.png`;
            }

            thumbnail.innerHTML =
            `<tr>
                <td colspan="2">
                    <img id="thumbnailImg" src="${data.imageUrl}"></img>
                </td>
            </tr>
            <tr>
                <td>클라우드 내부 경로</td>
                <td>/${data.filePath}</td>
            </tr>
            <tr>
                <td>이름</td>
                <td>${data.fileName}</td>
            </tr>
            <tr>
                <td>크기</td>
                <td>${data.size}</td>
            </tr>
            <tr>
                <td>마지막 수정일</td>
                <td>${data.lastModified}</td>
            </tr>`
        })
    .catch(error => {
        Swal.fire({
            title: "오류",
            html: "해당 파일을 불러오지 못했습니다.<br> 해당 에러가 주기적으로 발생시 문의해주세요.",
            icon: "error"
        });
    });
}

function folderThumbnail(searchFilePath ,folderName){
    const thumbnail = document.getElementById("thumbnail");
    const cloudPath = `${path}${folderName}`;
    thumbnail.innerHTML = "";

    if(searchFilePath === ""){
        thumbnail.innerHTML =
        `<tr>
            <td colspan="2">
                <img id="thumbnailImg" src="${contextPath}/resources/images/icon/icon-folder.png"></img>
            </td>
        </tr>
        <tr>
            <td>클라우드 내부 경로</td>
            <td>/${cloudPath}</td>
        </tr>
        <tr>
            <td>이름</td>
            <td>${folderName}</td>
        </tr>
        <tr>
            <td>크기</td>
            <td>-</td>
        </tr>
        <tr>
            <td>마지막 수정일</td>
            <td>-</td>
        </tr>`
    }else{
        thumbnail.innerHTML =
        `<tr>
            <td colspan="2">
                <img id="thumbnailImg" src="${contextPath}/resources/images/icon/icon-folder.png"></img>
            </td>
        </tr>
        <tr>
            <td>클라우드 내부 경로</td>
            <td>/${searchFilePath}</td>
        </tr>
        <tr>
            <td>이름</td>
            <td>${folderName}</td>
        </tr>
        <tr>
            <td>크기</td>
            <td>-</td>
        </tr>
        <tr>
            <td>마지막 수정일</td>
            <td>-</td>
        </tr>`        
    }
    
}

function navigateToParentFolder() {
    if (path === "" || path === "/") return; // 이미 최상위 폴더에 있으면 아무 작업도 하지 않음

    if (path === "" || path === "/") return; // 이미 최상위 폴더에 있으면 아무 작업도 하지 않음


    // 현재 path를 '/'로 나누고 마지막 요소를 제거
    const pathArray = path.split('/').filter(part => part); // 빈 요소 제거
    pathArray.pop(); // 마지막 폴더 제거

    // 새로운 path 재구성 (맨 앞에 슬래시를 붙이고 뒤에 슬래시를 붙이지 않음)
    path = pathArray.length > 0 ? `${pathArray.join('/')}/` : ""; 


    currentPathForm.value = "PublicCloud:/"+path.slice(0, -1);;

    // 새로운 경로에 대한 데이터 가져오기
    fetch(`${contextPath}/${companyId}/pubCloud`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `path=${encodeURIComponent(path)}`
    })
    .then(response => {
        if (!response.ok) {
            Swal.fire({
                title: "상위 폴더 이동 실패",
                text: "상위 폴더를 불러오는 중 문제가 발생했습니다.",
                icon: "error"
            });
            return;
        }
        return response.json(); // 서버로부터 폴더 및 파일 데이터 가져오기
    })
    .then(data => {
        sameFilesCheck = data.files || [];
        updateTable(data.folders, data.files); // 테이블 새로고침
    })
    .catch(error => {
        console.error("상위 폴더 이동 오류:", error);
        Swal.fire({
            title: "오류",
            text: "상위 폴더로 이동하지 못했습니다.",
            icon: "error"
        });
    });
}









