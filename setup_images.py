import os
import urllib.request
import json
import re

drawable_dir = r"d:\Semester\SEM6\SCM\prj\app\src\main\res\drawable"
os.makedirs(drawable_dir, exist_ok=True)

url = "https://dummyjson.com/products?limit=100"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
resp = urllib.request.urlopen(req)
all_data = json.loads(resp.read().decode('utf-8'))

groceries = [p for p in all_data['products'] if p['category'] in ['groceries', 'food', 'snacks', 'beauty', 'skin-care']]

kt_products = []
count = 0
for p in groceries:
    if count >= 20: break
    
    title = p['title'].replace('"', '')
    price = round(p['price'] * 5)
    img_url = p['thumbnail']
    
    local_name = f"pr_{count}.jpg"
    local_path = os.path.join(drawable_dir, local_name)
    try:
        urllib.request.urlretrieve(img_url, local_path)
    except Exception as e:
        print("Failed", img_url, e)
        continue 
        
    category = "Snacks"
    tl = title.lower()
    if 'apple' in tl or 'lemon' in tl or 'kiwi' in tl or 'beef' in tl or 'chicken' in tl or 'cucumber' in tl or 'pepper' in tl or 'tomato' in tl:
        category = "Vegetables"
    elif 'milk' in tl or 'cheese' in tl or 'butter' in tl or 'yogurt' in tl or 'cream' in tl or 'honey' in tl:
        category = "Dairy"
        
    res_url = f"android.resource://com.example.minigroceryapp/drawable/pr_{count}"
    kt_products.append(f'        Product({count+1}, "{title}", {price}.0, "{res_url}", "{category}")')
    count += 1

list_code = "private val allProducts = listOf(\n" + ",\n".join(kt_products) + "\n    )"
vm_path = r"d:\Semester\SEM6\SCM\prj\app\src\main\java\com\example\minigroceryapp\viewmodel\MainViewModel.kt"
with open(vm_path, "r", encoding="utf-8") as f:
    vm_code = f.read()

vm_code = re.sub(r'private val allProducts = listOf\(.*?\)', list_code, vm_code, flags=re.DOTALL)
with open(vm_path, "w", encoding="utf-8") as f:
    f.write(vm_code)

print("Created hardcoded fast loading assets directly into APK!")
