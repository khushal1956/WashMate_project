import re

file_path = "app/src/main/res/layout/activity_admin_dashboard.xml"
with open(file_path, "r") as f:
    text = f.read()

# 1. Remove bottom constraints that stretch the height and overlap
text = re.sub(r'app:layout_constraintBottom_toTopOf="@[^"]+"', '', text)

# 2. Add H,1:1
text = text.replace('app:layout_constraintDimensionRatio="1:1"', 'app:layout_constraintDimensionRatio="H,1:1"')

# 3. Increase padding inside the cells so the text isn't weirdly crunched
# 4. To ensure perfect circles, let's just make their constraints purely fixed sizes:
# Actually, H,1:1 combined with NO bottom constraints works flawlessly.
# Let's also ensure there is proper marginTop/marginBottom so they don't clip.
text = text.replace('android:layout_marginBottom="16dp"', 'android:layout_marginBottom="24dp"')
text = text.replace('android:layout_marginTop="8dp"', 'android:layout_marginTop="16dp"')

with open(file_path, "w") as f:
    f.write(text)

print("Patch applied")
