document.addEventListener("DOMContentLoaded", () => {
    fetch(`${contextPath}/${companyId}/hr/employee/myDepart/${empId}`)
        .then((response) => response.json())
        .then((data) => {
            const deptName = data.departName || "부서명 없음";
            document.querySelectorAll(".myDept").forEach((element) => {
                element.innerText = deptName;
            });
        })
        .catch((error) => console.error("에러 발생:", error));
});
