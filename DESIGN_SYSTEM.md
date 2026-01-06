# Classical Art Design System (古典艺术设计规范)

## 1. Design Philosophy (设计理念)
本设计系统致敬文艺复兴与巴洛克时期的艺术风格，旨在通过数字界面重现古典美学。
- **核心隐喻**: 画布与油画 (Canvas & Oil Painting)
- **关键词**: 沉稳 (Solemn), 华丽 (Ornate), 对称 (Symmetrical), 纸质感 (Parchment)

## 2. Color Palette (色彩方案)

### Primary Colors
- **Venetian Red (威尼斯红)**: `#7B1113` - 用于主标题、重要按钮背景、头部导航。
- **Prussian Blue (普鲁士蓝)**: `#003153` - 用于底部导航栏、深色背景区域。

### Secondary Colors
- **Gold Leaf (金箔)**: `#D4AF37` - 用于高亮、图标选中态、边框装饰、按钮渐变。
- **Ochre (赭石)**: `#CC7722` - 用于次级文本高亮、强调色。

### Neutral Colors
- **Ivory (象牙白)**: `#FFFFF0` - 全局背景色，模拟羊皮纸或画布底色。
- **Dark Sepia (深墨色)**: `#2F2725` - 主文本颜色，代替纯黑，更柔和。
- **Warm Grey (暖灰)**: `#5C5552` - 次级文本颜色。

## 3. Typography (排版)
- **Font Family**: Serif (衬线体) - 所有文本默认使用衬线体，模仿印刷品风格。
- **Spacing**: 标题字间距增加 (letterSpacing 0.05)，行距宽松 (lineSpacingMultiplier 1.2)。

## 4. UI Components (组件规范)

### Buttons (按钮)
- 采用拟物化设计，带有金属质感边框和渐变填充。
- **Primary Button**: 金色/赭石渐变背景 + 象牙白文字 + 浮雕效果。

### Cards (卡片)
- 背景色：象牙白 (`classical_ivory`)。
- 边框：双层边框设计。外层实线金边 (`classical_border_gold`)，内层虚线装饰。
- 阴影：保留柔和阴影 (`elevation: 6dp`) 以体现层次感。

### Icons (图标)
- 建议使用手绘风格或线描风格图标。
- 选中态使用金色 (`#D4AF37`)，未选中态使用象牙白或深色。

## 5. Navigation & Layout (导航与布局)

### Toolbar (顶部导航栏)
- **Background**: 威尼斯红 (`#7B1113`)。
- **Back Button (返回按钮)**:
    - 位置：统一位于页面左上角。
    - 样式：金色 (`#D4AF37`) 箭头图标 (`ic_back`)，无背景/透明背景。
    - 交互：点击返回上一级页面 (`finish()`)。
    - 适用范围：除主页 (`MainActivity`) 外的所有二级及以上页面。

### Course Detail Page (课程详情页)
- **Cover Display (封面展示)**:
    - 纯净模式：封面图片上不再覆盖播放按钮，保持画面完整性。
    - 交互变更：点击封面不再触发播放。
- **Video Playback (视频播放交互)**:
    - 触发方式：仅通过点击“课程目录” (Course Catalog) 列表中的具体章节触发播放。
    - 逻辑：点击章节 -> 跳转视频播放页 -> 传递视频地址。

## 6. Implementation Guide (开发指南)

### Styles
- `ClassicalTitleText`: 用于页面大标题。
- `ClassicalBodyText`: 用于正文内容。

### Drawables
- `bg_classical_card.xml`: 卡片背景。
- `bg_classical_button.xml`: 按钮背景。
- `bg_classical_screen.xml`: 页面通用背景。
- `include_toolbar.xml`: 统一的顶部导航栏布局，包含返回按钮和标题。

---
*Created by Trae AI Assistant*
