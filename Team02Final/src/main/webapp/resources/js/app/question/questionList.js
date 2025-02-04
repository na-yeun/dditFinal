/**
 * 
 */
document.addEventListener("DOMContentLoaded", ()=>{
    let $searchForm = $("#searchForm");
	let $searchBtn = $('.search-btn');
	
	$searchBtn.on("click", function(){
		let $parent = $(this).parents(".search-area");
		$parent.find(":input[name]").each(function(index, ipt){
			console.log(ipt.name, ipt.value);
			
			if(searchForm[ipt.name]){
				searchForm[ipt.name].value = ipt.value
			}
			
			$searchForm.submit();
		});
	});
});