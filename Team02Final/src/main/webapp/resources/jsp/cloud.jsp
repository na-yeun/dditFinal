<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>클라우드 시스템</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
        }

        .cloud-container {
            max-width: 800px;
            margin: 2rem auto;
            background: white;
            padding: 1.5rem;
            border: 1px solid #ddd;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .folder {
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin: 0.5rem 0;
            font-weight: bold;
        }

        .folder .icon {
            width: 20px;
            height: 20px;
        }

        .files {
            margin-left: 2rem;
            display: none;
        }

        .files.visible {
            display: block;
        }

        .file {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin: 0.3rem 0;
        }

        .file .icon {
            width: 20px;
            height: 20px;
        }
    </style>
</head>

<body>
    <header class="bg-primary text-white p-3 text-center">
        <h1>개인 클라우드</h1>
        <p>내 폴더와 파일을 관리하세요!</p>
    </header>

    <main class="cloud-container">
        <div class="folder" onclick="toggleFolder('folder1')">
            <img class="icon" src="https://cdn-icons-png.flaticon.com/512/715/715676.png" alt="folder-icon">
            개인 폴더 1
        </div>
        <div id="folder1" class="files">
            <div class="file">
                <img class="icon" src="https://cdn-icons-png.flaticon.com/512/685/685705.png" alt="file-icon">
                파일1.docx
            </div>
            <div class="folder" onclick="toggleFolder('subfolder1')">
                <img class="icon" src="https://cdn-icons-png.flaticon.com/512/715/715676.png" alt="folder-icon">
                서브폴더 1
            </div>
            <div id="subfolder1" class="files">
                <div class="file">
                    <img class="icon" src="https://cdn-icons-png.flaticon.com/512/685/685705.png" alt="file-icon">
                    서브파일1.pdf
                </div>
                <div class="file">
                    <img class="icon" src="https://cdn-icons-png.flaticon.com/512/685/685705.png" alt="file-icon">
                    서브파일2.jpg
                </div>
            </div>
        </div>

        <div class="folder" onclick="toggleFolder('folder2')">
            <img class="icon" src="https://cdn-icons-png.flaticon.com/512/715/715676.png" alt="folder-icon">
            개인 폴더 2
        </div>
        <div id="folder2" class="files">
            <div class="file">
                <img class="icon" src="https://cdn-icons-png.flaticon.com/512/685/685705.png" alt="file-icon">
                파일2.pptx
            </div>
            <div class="file">
                <img class="icon" src="https://cdn-icons-png.flaticon.com/512/685/685705.png" alt="file-icon">
                파일3.xlsx
            </div>
        </div>
    </main>

    <footer class="bg-dark text-white text-center p-3">
        <p>© 2024 개인 클라우드 시스템. 모든 권리 보유.</p>
    </footer>

    <script>
        function toggleFolder(folderId) {
            const folder = document.getElementById(folderId);
            folder.classList.toggle('visible');
        }
    </script>
</body>

</html>
