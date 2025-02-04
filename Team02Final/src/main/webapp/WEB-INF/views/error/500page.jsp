<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>500page</title>
<link href="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/css/core.css" rel="stylesheet">
<%-- <script type="text/javascript" src="${pageContext.request.contextPath }/resources/sneat-1.0.0/assets/vendor/js/bootstrap.js"></script> --%>
<style>
.custom-bg {
    background: linear-gradient(to right, #e2e8f0, #e5e7eb);
}

.custom-btn:hover {
    background-color: #f3e8ff !important;
    transition: background-color 0.3s ease-in-out;
}

@media (prefers-color-scheme: dark) {
    .custom-bg {
        background: linear-gradient(to right, #1f2937, #111827);
        color: white !important;
    }

    .custom-btn {
        background-color: #374151 !important;
        color: white !important;
    }

    .custom-btn:hover {
        background-color: #4b5563 !important;
    }
}

</style>

</head>
<body>
<div class="custom-bg text-dark">
    <div class="d-flex align-items-center justify-content-center min-vh-100 px-2">
        <div class="text-center">
            <h1 class="display-1 fw-bold">500</h1>
            <p class="fs-2 fw-medium mt-4">Oops! Internal Server Error</p>
            <p class="mt-4 mb-5">Unfortunately we're having trouble loading the page you are looking for. Please come back in a while.</p>
            <a href="${pageContext.request.contextPath }/${companyId}/main" class="btn btn-light fw-semibold rounded-pill px-4 py-2 custom-btn">
                Go Home
            </a>
        </div>
    </div>
</div>
</body>
</html>