# Sometimes it's a README fix, or something like that - which isn't relevant for
# including in a project's CHANGELOG for example
declared_trivial = github.pr_title.include? "#trivial"

# Make it more obvious that a PR is a work in progress and shouldn't be merged yet
warn("PR is classed as Work in Progress") if github.pr_title.include? "[WIP]"

# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 1

# Don't let testing shortcuts get into master by accident
fail("fdescribe left in tests") if `grep -r fdescribe specs/ `.length > 1
fail("fit left in tests") if `grep -r fit specs/ `.length > 1

#ENSURE THAT LABELS HAVE BEEN USED ON THE PR
failure "Please add labels to this PR" if github.pr_labels.empty?

#ENSURE THERE IS A SUMMARY FOR A PR
failure "Please provide a summary in the Pull Request description" if github.pr_body.length < 5

#HIGHLIGHT WITH A CLICKABLE LINK IF A gradle.properties IS CHANGED
warn "#{github.html_link("gradle.properties")} was edited." if git.modified_files.include? "gradle.properties"