import requests
from bs4 import BeautifulSoup
import pymysql

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

# 식당 ID → 이름 및 모드
restaurant_map = {
    'tr_box11_001': ('든든한동', 'multi'),
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
                    items = [line for line in items if line and "원산지" not in line if line != '=' if line[0] != '(' if line[0] != '-']
                else:
                    items = []
                for item in items:
                    menus.append({
                        "restaurant": restaurant,
                        "name": item,
                        "time": meals[i]
                    })
        else:
            text = tr.get_text(separator="\n").strip()
            lines = [line.strip() for line in text.split("\n") if line.strip()]
            items = [line for line in lines if "원산지" not in line if line != '-' if line[0] != '(' if line[0] != '-']
            for item in items:
                menus.append({
                    "restaurant": restaurant,
                    "name": item,
                    "time": None
                })

    return menus

def insert_to_db(menus):
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    for menu in menus:
        try:
            cursor.execute(
                "INSERT INTO food (restaurant, name, time) VALUES (%s, %s, %s)",
                (menu['restaurant'], menu['name'], menu['time'])
            )
        except Exception as e:
            print("Insert error:", e, menu)
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

