let allYearList = [];  // 서버에서 연도 가져와서 담는 용도도
let allScaleSize = [];
let allEmpCnt = [];
let allContType = [];

document.addEventListener("DOMContentLoaded" , () => {
    // fetchAndRenderOptions('/contract/readScaleList', 'selectScaleSize');
    // fetchAndRenderOptions('/provCommon/getContType', 'selectContractType');
    // fetchAndRenderOptions('/provCommon/getEmpcnt', 'selectEmpCnt');
    fetchAndRenderOptions('/prov/readOptionYears', 'selectYear');

}); //DOM end


async function fetchAndRenderOptions(url, elementId) {
    try {
        const response = await fetch(`${contextPath}${url}`);
        if (!response.ok) {
            throw new Error('데이터를 불러오는데 실패했습니다.');
        }
        const data = await response.json(); // JSON 데이터 파싱
        console.log("응답 데이터 : " , data);
        renderOptions(elementId, data); // 가져온 데이터로 옵션 렌더링
    } catch (error) {
        console.error('오류 발생:', error);
    }
}




// 옵션 렌더링 함수 
function renderOptions(elementId, optionList, selectedValue) {
    const selectElement = document.querySelector(`#${elementId}`);
    selectElement.innerHTML = '<option value="">전체</option>';  

    // 옵션 데이터가 비어있지 않은지 확인
    if (!Array.isArray(optionList) || optionList.length === 0) {
        console.error("옵션 리스트가 비어있거나 올바르지 않습니다.");
        return;
    }

    optionList.forEach(item => {
        let code = '';
        let name = '';

        // 데이터 구조 확인 및 적용
        if (item.contractType) {
            code = item.contractType;
            name = item.contractType;
        } 
        else if (item.scaleId) {
            code = item.scaleId;
            name = item.scaleSize;
        } 
        else if (item.empCountId) {
            code = item.empCountId;
            name = item.empCount;
        } 
        else if (typeof item === 'string') {
            code = item;
            name = item;
        } 
        else {
            code = "N/A";
            name = "이름 없음";
        }

        // 데이터 추가
        selectElement.insertAdjacentHTML("beforeend", `
            <option value="${code}" ${selectedValue === code ? 'selected' : ''}>
                ${name}
            </option>
        `);
    });
}








