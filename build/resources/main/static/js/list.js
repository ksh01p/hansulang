// src/main/resources/static/js/list.js
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
                tr.append(`<td>
          <button class="reviewBtn" data-id="${menu.id}">리뷰</button>
        </td>`);
                tbody.append(tr);
            });
            $('.reviewBtn').click(function(){
                window.location.href = '/board/detail/' + $(this).data('id');
            });
        }
    });
});
