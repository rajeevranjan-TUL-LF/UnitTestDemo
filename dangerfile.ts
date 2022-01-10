import { danger, fail, markdown, warn } from "danger"
// TypeScript thinks we're in React Native,
// so the node API gives us errors:
import * as fs from "fs"
import { pickBy } from "lodash"
import { ParseResult } from "./scripts/changelog/changelog-types"
import { changelogTemplateSections } from "./scripts/changelog/generateChangelogSectionTemplate"
import { parsePRDescription } from "./scripts/changelog/parsePRDescription"

/**
 * Helpers
 */
const typescriptOnly = (file: string) => file.includes(".ts")
const filesOnly = (file: string) => fs.existsSync(file) && fs.lstatSync(file).isFile()

// Modified or Created can be treated the same a lot of the time
const getCreatedFileNames = (createdFiles: string[]) => createdFiles.filter(filesOnly)

const testOnlyFilter = (filename: string) => filename.includes(".tests") && typescriptOnly(filename)

/**
 * Danger Rules
 */
// We are trying to migrate away from moment towards luxon
const preventUsingMoment = () => {
  const newMomentImports = getCreatedFileNames(danger.git.created_files).filter((filename) => {
    const content = fs.readFileSync(filename).toString()
    return content.includes('from "moment"') || content.includes('from "moment-timezone"')
  })
  if (newMomentImports.length > 0) {
    warn(`We are trying to migrate away from moment towards \`luxon\`, but found moment imports in the following new files:
${newMomentImports.map((filename) => `- \`${filename}\``).join("\n")}
See [docs](https://moment.github.io/luxon/api-docs/index.html).
  `)
  }
}

// We are trying to migrate away from Enzyme towards @testing-library/react-native
const preventUsingEnzyme = () => {
  const newEnzymeImports = getCreatedFileNames(danger.git.created_files)
    .filter(testOnlyFilter)
    .filter((filename) => {
      const content = fs.readFileSync(filename).toString()
      return content.includes('from "enzyme"')
    })
  if (newEnzymeImports.length > 0) {
    warn(`We are trying to migrate away from Enzyme towards \`@testing-library/react-native\`, but found Enzyme imports in the following new unit test files:
${newEnzymeImports.map((filename) => `- \`${filename}\``).join("\n")}
See [\`Pill.tests.tsx\`](https://github.com/artsy/eigen/blob/2f32d462bb3b4ce358c8a14e3ed09b42523de8bd/src/palette/elements/Pill/__tests__/Pill-tests.tsx) as an example, or [the docs](https://callstack.github.io/react-native-testing-library/docs/api-queries).
  `)
  }
}

// We are trying to migrate away from test-renderer towards @testing-library/react-native
const preventUsingTestRenderer = () => {
  const newTRImports = getCreatedFileNames(danger.git.created_files)
    .filter(testOnlyFilter)
    .filter((filename) => {
      const content = fs.readFileSync(filename).toString()
      return (
        content.includes('from "lib/tests/renderWithWrappers"') &&
        (content.includes("renderWithWrappers ") || content.includes("renderWithWrappers,"))
      )
    })
  if (newTRImports.length > 0) {
    warn(`We are trying to migrate away from \`react-test-renderer\` towards \`@testing-library/react-native\`, but found Test-Renderer imports in the following new unit test files:
${newTRImports.map((filename) => `- \`${filename}\``).join("\n")}
See [\`Pill.tests.tsx\`](https://github.com/artsy/eigen/blob/2f32d462bb3b4ce358c8a14e3ed09b42523de8bd/src/palette/elements/Pill/__tests__/Pill-tests.tsx) as an example, or [the docs](https://callstack.github.io/react-native-testing-library/docs/api-queries).
  `)
  }
}

const preventUsingRenderRelayTree = () => {
  const newRenderRelayTreeImports = getCreatedFileNames(danger.git.created_files)
    .filter(testOnlyFilter)
    .filter((filename) => {
      const content = fs.readFileSync(filename).toString()
      return content.includes('from "lib/tests/renderRelayTree"')
    })
  if (newRenderRelayTreeImports.length > 0) {
    warn(`We are trying to migrate away from \`renderRelayTree\` towards \`relay-test-utils\`, but found Enzyme imports in the following new unit test files:
  ${newRenderRelayTreeImports.map((filename) => `- \`${filename}\``).join("\n")}
  See [\`LoggedInUserInfo-tests.tsx\`](https://github.com/artsy/eigen/blob/f33577ebb09800224731365734be411b66ad8126/src/lib/Scenes/MyProfile/__tests__/LoggedInUserInfo-tests.tsx) as an example, or [the docs](https://relay.dev/docs/en/testing-relay-components).
    `)
  }
}

// Validates that we've not accidentally let in a testing
// shortcut to simplify dev work
const verifyRemainingDevWork = () => {
  const modified = danger.git.modified_files
  const editedFiles = modified.concat(danger.git.created_files)
  const testFiles = editedFiles.filter((f) => f?.includes("Tests") && f.match(/\.(swift|m)$/))

  const testingShortcuts = ["fdescribe(", "describe.only(", "fit(", "fit(", "it.only("]
  for (const file of testFiles) {
    const content = fs.readFileSync(file).toString()
    for (const shortcut of testingShortcuts) {
      if (content.includes(shortcut)) {
        fail(`Found a testing shortcut in ${file}`)
      }
    }
  }

  // A shortcut to say "I know what I'm doing thanks"
  const knownDevTools = danger.github.pr.body?.includes("#known") ?? false

  // These files are ones we really don't want changes to, except in really occasional
  // cases, so offer a way out.
  const devOnlyFiles = ["Artsy.xcodeproj/xcshareddata/xcschemes/Artsy.xcscheme"]
  for (const file of devOnlyFiles) {
    if (modified.includes(file) && !knownDevTools) {
      fail(
        "Developer Specific file shouldn't be changed, you can skip by adding #known to the PR body and re-running CI"
      )
    }
  }
}

// Require changelog on Eigen PRs to be valid
// See Eigen RFC: https://github.com/artsy/eigen/issues/4499
export const validatePRChangelog = () => {
  const pr = danger.github.pr

  const isOpen = pr.state === "open"
  if (!isOpen) {
    console.log("Skipping this check because the PR is not open")
    return
  }

  const content = pr.body

  const res = parsePRDescription(content) as ParseResult

  if (res.type === "error") {
    console.log("Something went wrong while parsing the PR description")
    warn("❌ **An error occurred while validating your changelog, please make sure you provided a valid changelog.**")
    return
  }

  if (res.type === "no_changes") {
    console.log("PR has no changes")
    warn("✅ **No changelog changes**")
    return
  }

  // At this point, the PR description changelog changes are valid
  // and res contains a list of the changes
  console.log("PR Changelog is valid")

  const { type, ...changedSections } = res

  const message =
    "### This PR contains the following changes:\n" +
    Object.entries(pickBy(changedSections, (changesArray) => changesArray.length))
      .map(([section, sectionValue]) => {
        return `\n- ${changelogTemplateSections[section as keyof typeof changedSections]} (${sectionValue})`
      })
      .join("")

  return markdown(message)
}

// Force the usage of WebPs
const IMAGE_EXTENSIONS_TO_AVOID = ["png", "jpg", "jpeg"]
const IMAGE_EXTENSIONS = [...IMAGE_EXTENSIONS_TO_AVOID, "webp"]

export const useWebPs = (fileNames: string[]) => {
  const hasNonWebImages = Boolean(
    fileNames
      .map((fileName) => fileName.split(".").pop() || "")
      .filter((fileExtension) => {
        return IMAGE_EXTENSIONS.includes(fileExtension)
      })
      .find((imageFileExtension) => IMAGE_EXTENSIONS_TO_AVOID.includes(imageFileExtension))
  )

  if (hasNonWebImages) {
    warn(
      "❌ **It seems like you added some non WebP images to Eigen, please convert them to WebPs using `source images/script.sh` script **"
    )
  }
}
;(async function () {
  const newCreatedFileNames = getCreatedFileNames(danger.git.created_files)

  preventUsingMoment()
  preventUsingEnzyme()
  preventUsingTestRenderer()
  preventUsingRenderRelayTree()
  verifyRemainingDevWork()
  validatePRChangelog()
  useWebPs(newCreatedFileNames)
})()