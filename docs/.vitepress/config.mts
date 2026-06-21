import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default async () => {

    let hoconGrammarFile = Bun.file("lang_grammar/hocon.json")
    let hoconGrammar = JSON.parse(await hoconGrammarFile.text())

    return defineConfig({
        title: "CustomCrafting",
        description: "Documentation for CustomCrafting",
        base: '/CustomCrafting/',
        head: [
            ['link', { rel: 'icon', href: '/CustomCrafting/favicon.svg' }]
        ],
        themeConfig: {
            logo: '/logo.svg',

            search: {
                provider: "local"
            },

            // https://vitepress.dev/reference/default-theme-config
            nav: [
                {text: 'Home', link: '/'},
                {text: 'Configuration', link: '/configuration/'},
            ],

            sidebar: [
                {
                    text: 'Getting Started', link: '/getting-started',
                },
                {
                    text: 'Configuration',
                    link: '/configuration/index',
                    items: [
                        {
                            text: 'Resources',
                            link: '/configuration/resources/index',
                            items: [
                                {
                                    text: 'Sources',
                                    link: '/configuration/resources/sources'
                                },
                                {
                                    text: 'Backup',
                                    link: '/configuration/resources/backup'
                                }
                            ]
                        }
                    ]
                },
                {
                    text: 'Recipes',
                    link: '/recipes/index',
                    items: [
                        {
                            text: 'Recipe Types',
                            items: [
                                {text: 'Crafting', link: '/recipes/crafting'},
                                {text: 'Cooking', link: '/recipes/cooking'},
                                {text: 'Grinding', link: '/recipes/grinding'},
                                {text: 'Mixing', link: '/recipes/mixing'},
                                {text: 'Repairing', link: '/recipes/repairing'},
                                {text: 'Smithing', link: '/recipes/smithing'},
                                {text: 'Stonecutting', link: '/recipes/stonecutting'},
                            ]
                        },
                        {text: 'Ingredients', link: '/recipes/ingredients'},
                        {text: 'Results', link: '/recipes/results'},
                    ]
                }
            ],

            socialLinks: [
                {icon: 'github', link: 'https://github.com/WolfyScript/CustomCrafting'},
                {icon: 'modrinth', link: 'https://modrinth.com/plugin/customcrafting'}
            ]
        },
        markdown: {
            languages: [
                {
                    aliases: [ "hocon" ],
                    ...hoconGrammar
                }
            ],
            languageAlias: {

            }
        }
    })
}
