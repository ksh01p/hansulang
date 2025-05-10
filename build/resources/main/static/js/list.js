$(function(){
    $.ajax({
        url: '/api/menus',
        method: 'GET',
        success: function(data){
            const tbody = $('#menuTableBody');
            data.forEach(menu => {
                const tr = $('<tr>');
                tr.append(`<td>${menu.id}</td>`);
                tr.append(`<td>${menu.name}</td>`);
                tr.append(`<td>${menu.price}</td>`);
                tr.append(`<td>${menu.description}</td>`);

                if (menu.imageUrl) {
                    tr.append(`<td><img src="${menu.imageUrl}" alt="${menu.name}" width="100"></td>`);
                } else {
                    tr.append(`<td>없음</td>`);
                }

                // 리뷰쓰기 버튼: /board/detail/{menuId} 경로로 이동
                tr.append(`<td>
                    <a href="/board/detail/${menu.id}" class="reviewBtn">리뷰쓰기</a>
                </td>`);

                tbody.append(tr);
            });
        },
        error: function() {
            alert("메뉴 불러오기 실패");
        }
    });
});
