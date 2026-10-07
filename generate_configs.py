import os

dungeons = [
    ('dungeon_e', '&8Cửa Hàng Dungeon E', 'TAN_BINH', 'ITEM_YEU_CAU_E', 1000, 500, 64),
    ('dungeon_d', '&8Cửa Hàng Dungeon D', 'CHIEN_BINH', 'ITEM_YEU_CAU_D', 2000, 1000, 128),
    ('dungeon_c', '&8Cửa Hàng Dungeon C', 'HIEP_SI', 'ITEM_YEU_CAU_C', 3000, 1500, 192),
    ('dungeon_b', '&8Cửa Hàng Dungeon B', 'TIEN_PHONG', 'ITEM_YEU_CAU_B', 4000, 2000, 256),
    ('dungeon_a', '&8Cửa Hàng Dungeon A', 'BONG_TOI', 'ITEM_YEU_CAU_A', 5000, 2500, 320),
    ('dungeon_a_plus_hoanguc', '&8Cửa Hàng Dungeon A+ Hỏa Ngục', 'HOA_NGUC', 'ITEM_YEU_CAU_A_PLUS_HOANGUC', 8000, 4000, 384),
    ('dungeon_a_plus_banggia', '&8Cửa Hàng Dungeon A+ Băng Giá', 'BAO_CHUA_BANG_GIA', 'ITEM_YEU_CAU_A_PLUS_BANGGIA', 8000, 4000, 448),
    ('dungeon_a_plus_dentho', '&8Cửa Hàng Dungeon A+ Đền Thờ', 'HAC_THIEN_SU', 'ITEM_YEU_CAU_A_PLUS_DENTHO', 8000, 4000, 512)
]

template = """title: "{title}"
layout:
  - "#########"
  - "# SBDHW #"
  - "#########"
  - "##12 34##"
  - "#########"

decorations:
  "#":
    material: GRAY_STAINED_GLASS_PANE
    name: "&7"

trades:
  "S":
    result:
      type: SWORD
      id: KIEM_{id_suffix}
      amount: 1
    cost:
      money: {w_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "B":
    result:
      type: BOW
      id: CUNG_{id_suffix}
      amount: 1
    cost:
      money: {w_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "D":
    result:
      type: DAGGER
      id: DAO_{id_suffix}
      amount: 1
    cost:
      money: {w_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "H":
    result:
      type: HAMMER
      id: BUA_{id_suffix}
      amount: 1
    cost:
      money: {w_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "W":
    result:
      type: STAFF
      id: GAY_{id_suffix}
      amount: 1
    cost:
      money: {w_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "1":
    result:
      type: ARMOR
      id: MU_{id_suffix}
      amount: 1
    cost:
      money: {a_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "2":
    result:
      type: ARMOR
      id: GIAP_{id_suffix}
      amount: 1
    cost:
      money: {a_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "3":
    result:
      type: ARMOR
      id: QUAN_{id_suffix}
      amount: 1
    cost:
      money: {a_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
  "4":
    result:
      type: ARMOR
      id: GIAY_{id_suffix}
      amount: 1
    cost:
      money: {a_money}.0
      items:
        req_item:
          type: MATERIAL
          id: {req_id}
          amount: {req_amt}
"""

output_dir = r'D:\.gemini\antigravity\scratch\GMTrade\TradeItem\GMTrade\menus'

for d_id, title, id_suffix, req_id, w_money, a_money, req_amt in dungeons:
    content = template.format(title=title, id_suffix=id_suffix, req_id=req_id, w_money=w_money, a_money=a_money, req_amt=req_amt)
    filepath = os.path.join(output_dir, f'{d_id}.yml')
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f'Generated {filepath}')
