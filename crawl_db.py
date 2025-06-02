import requests
from bs4 import BeautifulSoup
import pymysql
from datetime import datetime

# URL 설정
url = 'http://hisnet.handong.edu/login/login.php'

# DB 연결 정보
DB_CONFIG = {
    "host": "13.124.47.153",
    "user": "root",
    "password": "ivill0080@",
    "database": "ksh_sw",
    "charset": "utf8mb4"
}

# 식당 ID → 이름/모드
restaurant_map = {
    #'tr_box11_001': ('든든한동', 'multi'),
    'tr_box11_002': ('H:plate', 'single'),
    'tr_box11_003': ('Asian Market', 'single'),
    'tr_box11_005': ("Han's Deli", 'single'),
    'tr_box11_006': ('따스한동', 'single'),
    'tr_box11_2': ('맘스키친', 'multi'),
    'tr_box11_3': ('한동라운지', 'multi'),
    'tr_box11_4': ('더그레이스테이블', 'single'),
}

def parse_menu():
    res = requests.get(url)
    res.encoding = 'euc-kr'
    soup = BeautifulSoup(res.text, 'html.parser')

    menus = []
    now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    for box_id, (restaurant, mode) in restaurant_map.items():
        tr = soup.select_one(f"tr#{box_id}")
        if not tr:
            continue

        if mode == 'multi':
            tds = tr.select('td.cls_td_food_text td[align="left"]')
            meals = ['아침', '점심', '저녁']
            for i in range(3):
                if i < len(tds):
                    items = [line.strip() for line in tds[i].get_text(separator="\n").split("\n")]
                    items = [line for line in items if line and "원산지" not in line and line != '=' and not line.startswith('(') and not line.startswith('-')]
                else:
                    items = []
                for item in items:
                    menus.append({
                        "restaurant": restaurant,
                        "name": item,
                        "time": meals[i],
                        "created_at": now,
                        "updated_at": now
                    })
        else:
            text = tr.get_text(separator="\n").strip()
            lines = [line.strip() for line in text.split("\n") if line.strip()]
            items = [line for line in lines if "원산지" not in line and line != '-' and not line.startswith('(') and not line.startswith('-')]
            for item in items:
                menus.append({
                    "restaurant": restaurant,
                    "name": item,
                    "time": None,
                    "created_at": now,
                    "updated_at": now
                })

    return menus

def insert_to_db(menus):
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    for menu in menus:
        try:
            # 기존 레코드 확인
            cursor.execute(
                "SELECT id FROM menu_items WHERE restaurant = %s AND name = %s",
                (menu['restaurant'], menu['name'])
            )
            result = cursor.fetchone()

            if result:
                # 이미 존재하면 updated_at만 갱신
                cursor.execute(
                    "UPDATE menu_items SET updated_at = %s, time = %s WHERE id = %s",
                    (menu['updated_at'],menu['time'], result[0])
                )
                print(f"[UPDATE] {menu['restaurant']} - {menu['name']} [{menu['time']}]")
            else:
                # 없으면 새로 삽입
                cursor.execute(
                    "INSERT INTO menu_items(restaurant, name, time, created_at, updated_at, hisnet, recommend_count, not_recommend_count, price, user_id, image_url, description) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    (menu['restaurant'], menu['name'], menu['time'], menu['created_at'], menu['updated_at'], 1, 0, 0, 4500, 152, '', '학생식당')
                )
                print(f"[INSERT] {menu['restaurant']} - {menu['name']}")
        except Exception as e:
            print("[ERROR]", e, menu)

    conn.commit()
    cursor.close()
    conn.close()

if __name__ == '__main__':
    menu_list = parse_menu()
    print(f"[INFO] Parsed {len(menu_list)} menu items.")
    for m in menu_list:
        print(m)
    insert_to_db(menu_list)
    print("[INFO] Upload completed.")

