$(function(){
    let currentUser = null;

    // 1) 현재 로그인 사용자 정보
    $.get('/api/auth/me')
        .done(u => { currentUser = u; loadMenus(); })
        .fail(() => { loadMenus(); });

    // 2) 메뉴 리스트 로드
    function loadMenus(){
        $.ajax({
            url: '/api/menus',
            method: 'GET'
        }).done(menus => {
            const $c = $('#menuList').empty();
            menus.forEach(menu => {
                const imgSrc = menu.imageUrl
                    ? (menu.imageUrl.startsWith('/') ? menu.imageUrl : '/' + menu.imageUrl)
                    : '/images/placeholder.jpg';

                let buttons = `
                  <a href="/board/detail/${menu.id}"
                     class="inline-block mt-2 bg-black text-white px-3 py-1 rounded-full text-sm">
                    리뷰쓰기
                  </a>`;

                // 본인이 만든 메뉴면 삭제 버튼 추가
                if (currentUser && menu.createdById === currentUser.id) {
                    buttons += `
                      <button data-id="${menu.id}"
                              class="delete-menu-btn text-red-500 ml-2">
                        삭제
                      </button>`;
                }

                $c.append(`
                  <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200">
                    <img src="${imgSrc}" alt="${menu.name}"
                         class="w-full h-40 object-cover">
                    <div class="p-4">
                      <h3 class="text-lg font-semibold mb-1">${menu.name}</h3>
                      <p class="text-gray-600 mb-1">💰 ${menu.price}원</p>
                      <p class="text-sm text-gray-500 mb-2">${menu.description}</p>
                      <div>${buttons}</div>
                    </div>
                  </div>
                `);
            });
        }).fail(() => alert("메뉴 목록을 불러오는 데 실패했습니다."));
    }

    // 3) 메뉴 삭제 핸들러
    $(document).on('click', '.delete-menu-btn', function(){
        const id = $(this).data('id');
        if (!confirm('정말 메뉴를 삭제하시겠습니까?')) return;
        $.ajax({
            url: `/api/menus/${id}`,
            method: 'DELETE'
        }).done(loadMenus)
            .fail(() => alert('삭제에 실패했습니다.'));
    });
});
