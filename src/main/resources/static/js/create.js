// src/main/resources/static/js/create.js
$(function(){
    $('#createMenuForm').submit(function(e){
        e.preventDefault();
        const data = {
            name: $('input[name=name]').val(),
            price: parseFloat($('input[name=price]').val()),
            description: $('textarea[name=description]').val()
        };
        $.ajax({
            url: '/api/menus',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(){
                alert('메뉴 등록 성공');
                location.href = '/board/list';
            },
            error: function(){
                alert('메뉴 등록 실패');
            }
        });
    });
});
