# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140317045049) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "assets", force: true do |t|
    t.string   "number"
    t.string   "parent"
    t.string   "serialnum"
    t.string   "tag"
    t.string   "location"
    t.text     "description"
    t.string   "vendor"
    t.string   "failure_code"
    t.string   "manufacture"
    t.integer  "purchase_pri"
    t.float    "replace_cost"
    t.datetime "install_date"
    t.datetime "warranty_expire"
    t.float    "total_cost"
    t.float    "ytd_cost"
    t.float    "budget_cost"
    t.integer  "calnum"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "assets", ["number"], name: "index_assets_on_number", using: :btree

  create_table "check_manager", id: false, force: true do |t|
    t.integer "check_route_id", null: false
    t.integer "check_point_id", null: false
  end

  add_index "check_manager", ["check_point_id"], name: "index_check_manager_on_check_point_id", using: :btree
  add_index "check_manager", ["check_route_id"], name: "index_check_manager_on_check_route_id", using: :btree

  create_table "check_points", force: true do |t|
    t.integer  "cstm_tpmid"
    t.text     "description"
    t.integer  "hasld"
    t.string   "period_unit"
    t.string   "standard"
    t.string   "status"
    t.string   "hi_warn"
    t.integer  "period"
    t.string   "warn_type"
    t.string   "site_id"
    t.string   "tpm_num"
    t.string   "operator"
    t.string   "lo_warn"
    t.string   "tpm_type"
    t.string   "lo_danger"
    t.integer  "looknum"
    t.integer  "asset_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "check_points", ["cstm_tpmid"], name: "index_check_points_on_cstm_tpmid", using: :btree

  create_table "check_routes", force: true do |t|
    t.text     "description"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "comments", force: true do |t|
    t.string   "commenter"
    t.text     "body"
    t.integer  "post_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "comments", ["post_id"], name: "index_comments_on_post_id", using: :btree

  create_table "posts", force: true do |t|
    t.string   "title"
    t.text     "text"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
