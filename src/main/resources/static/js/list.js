$(function(){
    $.ajax({
        url: '/api/menus',
        method: 'GET',
        success: function(data){
            const container = $('#menuList').empty();

            data.forEach(menu => {
                // 이미지 URL이 없으면 placeholder, 있으면 앞에 '/' 추가 여부 확인
                const imgSrc = menu.imageUrl
                    ? (menu.imageUrl.startsWith('/') ? menu.imageUrl : '/' + menu.imageUrl)
                    : '/images/placeholder.jpg';

                const card = `
                    <div class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200">
                        <img src="${imgSrc}" alt="${menu.name}" class="w-full h-40 object-cover">
                        <div class="p-4">
                            <h3 class="text-lg font-semibold mb-1">${menu.name}</h3>
                            <p class="text-gray-600 mb-1">💰 ${menu.price}원</p>
                            <p class="text-sm text-gray-500 mb-2">${menu.description}</p>
                            <a href="/board/detail/${menu.id}" class="inline-block mt-2 bg-black text-white px-3 py-1 rounded-full text-sm">
                                리뷰쓰기
                            </a>
                        </div>
                    </div>
                `;
                container.append(card);
            });
        },
        error: function(){
            alert("메뉴 목록을 불러오는 데 실패했습니다.");
        }
    });
});
