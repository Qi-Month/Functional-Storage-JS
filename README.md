# A KubeJS Addon for Functional Storage

A KubeJS addon that allows you to directly register **Functional Storage Drawers and Storage Upgrades** through KubeJS scripts.

## Adding Drawers

```js
FunctionalStorageJSEvents.register((event) => {
    event.addDrawer("rubberwood", (builder) => {
        builder.log("minecraft:stone")
            .planks("minecraft:stone_bricks")
            .frontTexture("kubejs:block/aaaa/rubberwood_front")
            .sideTexture("kubejs:block/aaaa/rubberwood_side")
    })
})
````

Registering a drawer will automatically register the **1x1, 1x2, and 2x2** variants.

## Adding Storage Upgrades

```js
FunctionalStorageJSEvents.register((event) => {
    event.addUpgrade("rose_gold", (builder) => {
        builder.multiplier(8)
            .fluidMultiplier(4)
            .rangeMultiplier(2)
            .texture("kubejs:item/upgrade/rose_gold")
            .foil(true)
            .addToTab(true)
    })
})
```

## Requirements:
 - [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs)
 - [Functional Storage](https://www.curseforge.com/minecraft/mc-mods/functional-storage)
 - as well as their prerequisites

---

# 一个 KubeJS 附属模组, 让你可以直接通过 KubeJS 脚本注册 功能性存储 的 储物抽屉 和 存储升级

## 添加抽屉
```js
FunctionalStorageJSEvents.register((event) => {
    event.addDrawer("cmi:rubberwood", (builder) => {
        builder.log("minecraft:stone")
            .planks("minecraft:stone_bricks")
            .frontTexture("kubejs:block/aaaa/rubberwood_front")
            .sideTexture("kubejs:block/aaaa/rubberwood_side")
    })
})
```

注册时会同时注册`1x1`, `1x2`和`2x2`的抽屉

## 添加存储升级
```js
FunctionalStorageJSEvents.register((event) => {
	event.addUpgrade("rose_gold", (builder) => {
		builder.multiplier(8)
			.fluidMultiplier(4)
			.rangeMultiplier(2)
			.texture("kubejs:item/upgrade/rose_gold")
			.foil(true)
			.addToTab(true)
	})
})
```

## 需要:
- [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs)
- [功能性存储](https://www.curseforge.com/minecraft/mc-mods/functional-storage)
- 以及他们的前置